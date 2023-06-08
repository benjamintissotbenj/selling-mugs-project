package com.benjtissot.sellingmugs.services

import com.benjtissot.sellingmugs.controllers.artworkCollection
import com.benjtissot.sellingmugs.controllers.mugCollection
import com.benjtissot.sellingmugs.entities.Artwork
import com.benjtissot.sellingmugs.genUuid
import com.benjtissot.sellingmugs.repositories.clickCollection
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import org.litote.kmongo.eq
import org.litote.kmongo.upsert

class ArtworkService {
    companion object {

        suspend fun getArtworkList() : List<Artwork> {
            return artworkCollection.find().toList()
        }

        suspend fun insertNewArtwork(artwork: Artwork){
            artworkCollection.insertOne(artwork)
        }

        suspend fun updateArtwork(artwork: Artwork){
            // TODO : replace evey "if !it.wasAcknowledged()" by upsert()
            artworkCollection.updateOneById(artwork.id, artwork, upsert())
        }

        suspend fun PipelineContext<*, ApplicationCall>.deleteArtwork(id: String){
            mugCollection.deleteOne(Artwork::id eq id) //type safe
            call.respond(HttpStatusCode.OK)
        }
    }
}