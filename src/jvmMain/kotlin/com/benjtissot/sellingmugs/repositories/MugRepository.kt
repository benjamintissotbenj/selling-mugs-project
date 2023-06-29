package com.benjtissot.sellingmugs.repositories

import com.benjtissot.sellingmugs.controllers.artworkCollection
import com.benjtissot.sellingmugs.entities.Artwork
import com.benjtissot.sellingmugs.entities.Mug
import database
import org.litote.kmongo.MongoOperator
import org.litote.kmongo.and
import org.litote.kmongo.eq
import org.litote.kmongo.upsert

val mugCollection = database.getCollection<Mug>()

class MugRepository {
    companion object {

        /**
         * @param mug the [Mug] to be inserted
         */
        suspend fun insertMug(mug: Mug) {
            mugCollection.insertOne(mug)
        }

        /**
         * @param mug the [Mug] to be updated (inserted if not existent)
         */
        suspend fun updateMug(mug: Mug) {
            mugCollection.updateOneById(mug.id, mug, upsert())
        }

        suspend fun getMugByPrintifyId(printifyId: String) : Mug? {
            return mugCollection.findOne(Mug::printifyId eq printifyId)
        }

        suspend fun getMugByArtwork(artwork: Artwork) : Mug? {
            return mugCollection.findOne(Mug::artwork eq artwork)
        }

    }
}