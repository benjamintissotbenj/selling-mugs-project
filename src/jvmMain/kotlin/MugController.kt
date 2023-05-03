import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.litote.kmongo.eq

val mugCollection = database.getCollection<Mug>()
fun Route.mugRouting(){

    route(Mug.path) {
        get {
            call.respond(mugCollection.find().toList())
        }
        post {
            mugCollection.insertOne(call.receive<Mug>().copy(genUuid().toString()))
            call.respond(HttpStatusCode.OK)
        }
        delete("/{id}") {
            val id = call.parameters["id"] ?: error("Invalid delete request")
            mugCollection.deleteOne(Mug::id eq id) //type safe
            call.respond(HttpStatusCode.OK)
        }
    }
}