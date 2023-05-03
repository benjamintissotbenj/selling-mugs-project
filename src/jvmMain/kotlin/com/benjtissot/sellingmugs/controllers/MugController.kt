package com.benjtissot.sellingmugs.controllers

import com.benjtissot.sellingmugs.entities.Artwork
import com.benjtissot.sellingmugs.entities.Mug
import database
import com.benjtissot.sellingmugs.genUuid
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.litote.kmongo.eq
import java.util.logging.Logger


val mugCollection = database.getCollection<Mug>()
val artworkCollection = database.getCollection<Artwork>()
fun Route.mugRouting(){

    val LOG = Logger.getLogger(this.javaClass.name)

    route(Mug.path) {
        get {
            call.respond(mugCollection.find().toList())
        }
        post {
            LOG.severe("Posting Mug")
            mugCollection.insertOne(call.receive<Mug>().copy(genUuid().toString()))
            call.respond(HttpStatusCode.OK)
        }
        delete("/{id}") {
            val id = call.parameters["id"] ?: error("Invalid delete request")
            mugCollection.deleteOne(Mug::id eq id) //type safe
            call.respond(HttpStatusCode.OK)
        }
    }

    route(Artwork.path) {
        get {
            call.respond(artworkCollection.find().toList())
        }
        post {
            LOG.severe("Posting Artwork")
            artworkCollection.insertOne(call.receive<Artwork>().copy(genUuid().toString()))
            call.respond(HttpStatusCode.OK)
        }
        delete("/{id}") {
            val id = call.parameters["id"] ?: error("Invalid delete request")
            mugCollection.deleteOne(Artwork::id eq id) //type safe
            call.respond(HttpStatusCode.OK)
        }
    }
}