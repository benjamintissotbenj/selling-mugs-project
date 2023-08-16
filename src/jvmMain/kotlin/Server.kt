import ch.qos.logback.classic.LoggerContext
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.benjtissot.sellingmugs.ConfigConst
import com.benjtissot.sellingmugs.Const
import com.benjtissot.sellingmugs.HOMEPAGE_PATH
import com.benjtissot.sellingmugs.controllers.*
import com.benjtissot.sellingmugs.entities.local.Mug
import com.benjtissot.sellingmugs.entities.local.Session
import com.benjtissot.sellingmugs.entities.openAI.CategoriesChatRequestParams
import com.benjtissot.sellingmugs.entities.openAI.OpenAIUnavailable
import com.benjtissot.sellingmugs.repositories.CategoriesGenerationResultRepository
import com.benjtissot.sellingmugs.repositories.MugRepository
import com.benjtissot.sellingmugs.services.ImageGeneratorService
import com.benjtissot.sellingmugs.services.MugService
import com.mongodb.ConnectionString
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.util.logging.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import java.util.*
import java.util.concurrent.TimeUnit


// Connection String for MongoDB in Heroku
val connectionString: ConnectionString? = System.getenv(Const.MONGODB_URI_STRING)?.let {
    ConnectionString(it)
}

val client =
    if (connectionString != null) {
        KMongo.createClient(connectionString).coroutine
    } else {
        KMongo.createClient().coroutine
    }

var database = client.getDatabase(System.getenv(Const.MONGODB_DBNAME_STRING) ?: "debug")
var redirectPath = ""

private val LOG = KtorSimpleLogger("Server.kt")

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)
fun Application.module() {
    embeddedServer(Netty, ConfigConst.PORT) {


        // Deactivating MongoDb Driver logs
        deactivateMongoDriverLogs()


        // Automatic content conversion from the requests, based on the headers (content-type and accept)
        // Basically delegates json (de)serialisation to the KTOR framework
        install(ContentNegotiation) {
            json()
        }

        // Install logging of the calls made to this server
        // Idea is to log only bad requests
        install(CallLogging) {
            level = Level.INFO
            filter { call ->
                call.response.status() != HttpStatusCode.OK
            }
            format { call ->
                val status = call.response.status()
                val httpMethod = call.request.httpMethod.value
                var queryParams : String = ""
                call.request.queryParameters.forEach {name, values ->
                    queryParams += "$name=${if (values.isNotEmpty()) values[0] else ""}}"
                }
                "$httpMethod method on ${call.request.path()}, query params ${queryParams}, Status: $status"
            }
        }

        // Cross Origin Resource Sharing, handles calls to arbitrary JS clients
        install(CORS) {
            allowMethod(HttpMethod.Get)
            allowMethod(HttpMethod.Post)
            allowMethod(HttpMethod.Delete)
            allowMethod(HttpMethod.Put)
            allowHeader(HttpHeaders.AccessControlAllowOrigin)
            allowHeader(HttpHeaders.AccessControlAllowHeaders)
            allowHeader(HttpHeaders.AccessControlAllowCredentials)
            allowHeader(HttpHeaders.Origin)
            allowHeader(HttpHeaders.ContentType)
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

        // Creates the routing for the application
        createRoutes()

        // Run mug creation every day
        scheduleMugCreation()

    }.start(wait = true)
}

fun Application.createRoutes(){
    /*val routing = */routing {
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
        openAIRouting()

        // Static to access resources (index.html, sellingmugs.js)
        static("/static") {
            resources("")
        }

        // Any other route redirects to homepage
        get("/{${Const.path}}/{${Const.param}...}"){
            val path = call.parameters[Const.path] ?: error("Invalid get request")
            redirectPath = if (path == Const.productInfo) {
                val param = call.parameters.getAll(Const.param)?.get(0) ?: error("Invalid get request")
                if (param == "favicon.ico" || param == "static"){
                    ""
                } else {
                    "/$path/$param"
                }
            } else if (call.parameters.getAll(Const.param)?.isEmpty() == false){
                  if (call.parameters.getAll(Const.param)?.get(0) == "favicon.ico"){
                      ""
                  } else {
                      "/$path"
                  }
            } else {
                "/$path"
            }
            LOG.info("Redirecting to homepage to load page and redirect from front-end")
            call.respondRedirect(HOMEPAGE_PATH)
        }

    }

    // Print out all the routes for debug
    // allRoutes(routing).forEach { println(it) }
}

@OptIn(DelicateCoroutinesApi::class)
fun Application.scheduleMugCreation(){
    // We're setting mug creations every day at 18h00m00
    val today = Calendar.getInstance()
    today[Calendar.HOUR_OF_DAY] = 11
    today[Calendar.MINUTE] = 05
    today[Calendar.SECOND] = 0

    val timer = Timer()
    val task: TimerTask = object : TimerTask() {
        override fun run() {
            // do your task here
            LOG.debug("Starting daily mug creation")
            GlobalScope.launch {
                val status = try {
                    ImageGeneratorService.generateCategoriesAndMugs(
                        CategoriesChatRequestParams(
                            1,
                            10,
                            null,
                            false
                        )
                    )?.let { CategoriesGenerationResultRepository.updateGenerateCategoriesStatus (it) }
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
                if (status == null){
                    LOG.error("There was an internal server error")
                } else if (status.message == OpenAIUnavailable().message){
                    LOG.error("Mug creation failed:")
                    LOG.error(status.message)
                } else if (status.message.contains("Exceeded five tries")){
                    LOG.error("Mug creation failed:")
                    LOG.error(status.message)
                } else {
                    LOG.debug("Mug creation successful:")
                    LOG.debug(status.message)
                }
            }
        }
    }
    // Start at 12:00:00, repeat every hour
    timer.schedule(task, today.time, TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS))
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
            challenge { _, _ ->
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