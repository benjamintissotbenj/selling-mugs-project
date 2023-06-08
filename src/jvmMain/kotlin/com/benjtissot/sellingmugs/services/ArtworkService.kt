package com.benjtissot.sellingmugs.services

import com.benjtissot.sellingmugs.controllers.artworkCollection
import com.benjtissot.sellingmugs.controllers.mugCollection
import com.benjtissot.sellingmugs.entities.Artwork
import io.ktor.http.*
import io.ktor.server.application.*
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
            artworkCollection.updateOneById(artwork.id, artwork, upsert())
        }

        suspend fun findArtworkByPrintifyId(printifyId: String) : Artwork? {
            return artworkCollection.findOne(Artwork::printifyId eq printifyId)
        }

        suspend fun PipelineContext<*, ApplicationCall>.deleteArtwork(id: String){
            mugCollection.deleteOne(Artwork::id eq id) //type safe
            call.respond(HttpStatusCode.OK)
        }
    }
}