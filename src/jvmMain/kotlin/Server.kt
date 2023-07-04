import ch.qos.logback.classic.LoggerContext
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.benjtissot.sellingmugs.ConfigConst
import com.benjtissot.sellingmugs.Const
import com.benjtissot.sellingmugs.HOMEPAGE_PATH
import com.benjtissot.sellingmugs.controllers.*
import com.benjtissot.sellingmugs.entities.Session
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.util.logging.*
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import org.slf4j.LoggerFactory
import java.io.File
import java.net.URI


var client = KMongo.createClient().coroutine
var database = client.getDatabase("debug")
var redirectPath = ""

private val LOG = KtorSimpleLogger("Server.kt")

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)
fun Application.module() {
    embeddedServer(Netty, System.getenv(Const.PORT).toInt()) {
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

        // Provides authentication via JWT
        installAuthentication()

        // Handling session
        install(Sessions){
            cookie<Session>("session"){
                cookie.maxAgeInSeconds = 600
            }
            cookie<String>("redirectURL"){
                cookie.maxAgeInSeconds = 600
            }
        }

        // Deactivating MongoDb Driver logs
        deactivateMongoDriverLogs()

        // Creates the routing for the application
        createRoutes()

    }.start(wait = true)
}

fun Application.createRoutes(){
    val routing = routing {


        // Routing to the controllers
        homepageRouting()
        sessionRouting()
        clickRouting()
        mugRouting()
        userInfoRouting()
        loginRouting()
        cartRouting()
        checkoutRouting()
        paymentRouting()
        userRouting()
        printifyRouting()
        checkRedirectRouting()
        orderRouting()

        // Static to access resources (index.html, sellingmugs.js)
        static("/static") {
            resources("")
        }
        // Accessing the icon for the browser
        get("/favicon.ico") {
            call.respondFile(File(URI(this::class.java.classLoader.getResource("static/icon.jpg")?.toString()?:""))) {
                ContentType.Image.JPEG
            }
        }
        // Accessing the icon for the browser
        get("/print_template.png") {
            call.respondFile(File(URI(this::class.java.classLoader.getResource("static/print_template_mug.png")?.toString()?:""))) {
                ContentType.Image.PNG
            }
        }

        // Any other route redirects to homepage
        get("/{path}"){
            val path = call.parameters["path"] ?: error("Invalid get request")
            redirectPath = "/$path"
            LOG.info("Redirecting to homepage to load page and redirect from front-end")
            call.respondRedirect(HOMEPAGE_PATH)
        }

    }

    // Print out all the routes for debug
    // allRoutes(routing).forEach { println(it) }
}

fun deactivateMongoDriverLogs(){
    val loggerContext: LoggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
    val rootLogger = loggerContext.getLogger("org.mongodb.driver")
    rootLogger.level = ch.qos.logback.classic.Level.OFF
    LOG.error("MongoDB Driver Logs deactivated")
}

/**
 * Method used to configure authentication method via JWT
 */
fun Application.installAuthentication(){

    val secret = ConfigConst.SECRET
    val issuer = ConfigConst.ISSUER
    val audience = ConfigConst.AUDIENCE
    val myRealm = ConfigConst.REALM

    install(Authentication){

        jwt("auth-jwt") {
            realm = myRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(secret))
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .build())
            // Checking the token credential
            validate { credential ->
                LOG.info("validating credentials")
                if (credential.payload.getClaim("email").asString() != "") {
                    LOG.info("validating email ${credential.payload.getClaim("email")}")
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            // What to do if token is not valid
            challenge { defaultScheme, realm ->
                call.respond(HttpStatusCode.Unauthorized)
            }
        }

    }
}

/**
 * Prints all the routes from the root
 */
fun allRoutes(root: Route): List<Route> {
    return listOf(root) + root.children.flatMap { allRoutes(it) }
}