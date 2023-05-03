import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.litote.kmongo.eq

val cartCollection = database.getCollection<Cart>()
fun Route.cartRouting(){


    route(Cart.path) {
        get {
            call.respond(cartCollection.find().toList())
        }
        post {
            cartCollection.insertOne(call.receive<Cart>().copy(genUuid().toString()))
            call.respond(HttpStatusCode.OK)
        }
        delete("/{id}") {
            val id = call.parameters["id"] ?: error("Invalid delete request")
            cartCollection.deleteOne(Cart::id eq id) //type safe
            call.respond(HttpStatusCode.OK)
        }
    }
}