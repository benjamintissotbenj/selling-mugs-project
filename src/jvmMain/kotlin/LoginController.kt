import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.loginRouting(){

    route(LOGIN_PATH) {
        get {
            call.respond("Hello Login Controller")
        }
        post {
            call.respond(HttpStatusCode.OK)
        }
        delete() {
            call.respond(HttpStatusCode.OK)
        }
    }

    route(REGISTER_PATH) {
        get {
            call.respond("Hello Register Controller")
        }
        post {
            call.respond(HttpStatusCode.OK)
        }
        delete() {
            call.respond(HttpStatusCode.OK)
        }
    }
}