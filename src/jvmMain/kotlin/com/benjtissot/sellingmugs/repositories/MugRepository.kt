package com.benjtissot.sellingmugs.repositories

import com.benjtissot.sellingmugs.controllers.artworkCollection
import com.benjtissot.sellingmugs.entities.Artwork
import com.benjtissot.sellingmugs.entities.Mug
import com.benjtissot.sellingmugs.entities.User
import com.benjtissot.sellingmugs.entities.UserCustomMugList
import com.benjtissot.sellingmugs.entities.printify.order.Order

import database
import org.litote.kmongo.*

val mugCollection = database.getCollection<Mug>()
val userCustomMugCollection = database.getCollection<UserCustomMugList>()

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

    }
}