import ch.qos.logback.classic.LoggerContext
import com.benjtissot.sellingmugs.Const
import com.benjtissot.sellingmugs.HOMEPAGE_PATH
import com.benjtissot.sellingmugs.repositories.SessionRepository
import com.benjtissot.sellingmugs.controllers.*
import com.benjtissot.sellingmugs.entities.Click
import com.benjtissot.sellingmugs.entities.Session
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import org.slf4j.LoggerFactory


val client = KMongo.createClient().coroutine
val database = client.getDatabase("debug")

fun main() {
    embeddedServer(Netty, 9090) {
        // Automatic content conversion from the requests, based on the headers (content-type and accept)
        // Basically delegates json (de)serialisation to the KTOR framework
        install(ContentNegotiation) {
            json()
        }

        // Cross Origin Resource Sharing, handles calls to arbitrary JS clients
        install(CORS) {
            allowMethod(HttpMethod.Get)
            allowMethod(HttpMethod.Post)
            allowMethod(HttpMethod.Delete)
            anyHost()
        }

        // Self-explanatory
        install(Compression) {
            gzip()
        }

        // Provides authentication
        install(Authentication)

        // Handling session
        install(Sessions){
            cookie<Session>("session"){
                cookie.maxAgeInSeconds = 600
            }
        }

        // Creates the routing for the application
        createRoutes()

    }.start(wait = true)
}

fun Application.createRoutes(){
    val routing = routing {
        // When getting on the empty URL, create session and redirect to homepage
        get("/") {
//                val newSession = SessionRepository.createSession()
//                call.sessions.set(newSession)
            call.respondRedirect(HOMEPAGE_PATH)
        }
        static("/") {
            resources("")
        }

        get("/hello") {
            call.respondText(
                this::class.java.classLoader.getResource("index.html")!!.readText(),
                ContentType.Text.Html
            )
        }
        static("/hello") {
            resources("")
        }

        // Routing to my controllers

        sessionRouting()
        clickRouting()
        homepageRouting()
        mugRouting()

        /*loginRouting()
        cartRouting()
        checkoutRouting()
        paymentRouting()*/

        val loggerContext: LoggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
        val rootLogger = loggerContext.getLogger("org.mongodb.driver")
        rootLogger.level = ch.qos.logback.classic.Level.OFF

        val LOG = java.util.logging.Logger.getLogger(this.javaClass.name)
        LOG.severe("MongoDB Driver Logs deactivated")
    }

    // Print out all the routes for debug
    allRoutes(routing).forEach { println(it) }
}

/**
 * Prints all the routes from the root
 */
fun allRoutes(root: Route): List<Route> {
    return listOf(root) + root.children.flatMap { allRoutes(it) }
}