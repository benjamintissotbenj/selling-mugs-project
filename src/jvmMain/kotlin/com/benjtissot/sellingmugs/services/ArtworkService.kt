package com.benjtissot.sellingmugs.services

import com.benjtissot.sellingmugs.controllers.artworkCollection
import com.benjtissot.sellingmugs.controllers.mugCollection
import com.benjtissot.sellingmugs.entities.Artwork
import com.benjtissot.sellingmugs.genUuid
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import org.litote.kmongo.eq

class ArtworkService {
    companion object {

        suspend fun PipelineContext<*, ApplicationCall>.getArtworkList(){
            call.respond(artworkCollection.find().toList())
        }

        suspend fun PipelineContext<*, ApplicationCall>.insertNewArtwork(){
            artworkCollection.insertOne(call.receive<Artwork>().copy(genUuid().toString()))
            call.respond(HttpStatusCode.OK)
        }

        suspend fun PipelineContext<*, ApplicationCall>.deleteArtwork(id: String){
            mugCollection.deleteOne(Artwork::id eq id) //type safe
            call.respond(HttpStatusCode.OK)
        }
    }
}