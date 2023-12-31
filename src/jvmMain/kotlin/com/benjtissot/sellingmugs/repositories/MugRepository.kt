package com.benjtissot.sellingmugs.repositories

import com.benjtissot.sellingmugs.entities.local.Artwork
import com.benjtissot.sellingmugs.entities.local.Mug
import com.benjtissot.sellingmugs.entities.local.User
import com.benjtissot.sellingmugs.entities.local.UserCustomMugList
import com.benjtissot.sellingmugs.entities.printify.ProductLog
import database
import org.litote.kmongo.eq
import org.litote.kmongo.push
import org.litote.kmongo.upsert

val mugCollection = database.getCollection<Mug>()
val userCustomMugCollection = database.getCollection<UserCustomMugList>()
val productLogCollection = database.getCollection<ProductLog>()

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
        suspend fun updateMug(mug: Mug) : Mug {
            mugCollection.updateOneById(mug.id, mug, upsert())
            return mug
        }

        suspend fun getMugByPrintifyId(printifyId: String) : Mug? {
            return mugCollection.findOne(Mug::printifyId eq printifyId)
        }

        suspend fun getMugByUrlHandle(urlHandle: String) : Mug? {
            return mugCollection.findOne(Mug::urlHandle eq urlHandle)
        }

        suspend fun getMugById(mugId: String) : Mug? {
            return mugCollection.findOne(Mug::id eq mugId)
        }

        suspend fun getMugByArtwork(artwork: Artwork) : Mug? {
            return mugCollection.findOne(Mug::artwork eq artwork)
        }


        /**
         * Adds an [Mug.id] to a [User]'s list of custom mugs
         * @param userId the id of the [User] for which to retrieve the list of custom [Mug]s
         * @param mugId the local id of the [Mug] to be added to the list
         */
        suspend fun addMugToUserCustomMugList(userId: String, mugId: String){
            userCustomMugCollection.updateOneById(userId, push(UserCustomMugList::mugIds, mugId), upsert())
        }

        /**
         * Creates a user's list of custom mugs in the database
         * @param userCustomMugList the item to insert
         */
        suspend fun insertUserCustomMugList(userCustomMugList: UserCustomMugList){
            userCustomMugCollection.insertOne(userCustomMugList)
        }

        /**
         * Retrieves a user's list of custom mugs
         * @param userId the id of the [User] for which to retrieve the list of past custom [Mug]s they've created
         * @return a [UserCustomMugList] if the list exists, null otherwise (shouldn't happen, but we never know)
         */
        suspend fun getUserCustomMugListByUserId(userId: String) : UserCustomMugList? {
            return userCustomMugCollection.findOneById(userId)
        }

        /**
         * Inserts a [ProductLog] into the database
         */
        suspend fun insertProductLog(productLog: ProductLog) {
            productLogCollection.insertOne(productLog)
        }

    }
}