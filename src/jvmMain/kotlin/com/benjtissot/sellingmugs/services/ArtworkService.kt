package com.benjtissot.sellingmugs.services

import com.benjtissot.sellingmugs.controllers.artworkCollection
import com.benjtissot.sellingmugs.entities.Artwork
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

        suspend fun updateArtwork(artwork: Artwork) : Artwork {
            artworkCollection.updateOneById(artwork.id, artwork, upsert())
            return artwork
        }

        suspend fun findArtworkByPrintifyId(printifyId: String) : Artwork? {
            return artworkCollection.findOne(Artwork::printifyId eq printifyId)
        }

        suspend fun deleteArtwork(id: String){
            artworkCollection.deleteOne(Artwork::id eq id) //type safe
        }
    }
}