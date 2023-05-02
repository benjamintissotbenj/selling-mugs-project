import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.paymentRouting(){

    route(PAYMENT_PATH) {
        get {
            call.respond("Hello Payment Controller")
        }
        post {
            call.respond(HttpStatusCode.OK)
        }
        delete() {
            call.respond(HttpStatusCode.OK)
        }
    }
}