package com.benjtissot.sellingmugs.repositories

import com.benjtissot.sellingmugs.entities.User
import database

val userCollection = database.getCollection<User>()

class UserRepository {
    companion object {

        /**
         * @param user the [User] to be inserted
         */
        suspend fun insertUser(user: User) {
            userCollection.insertOne(user)
        }

        /**
         * @param user the [User] to be updated (inserted if not existent)
         */
        suspend fun updateUser(user: User) {
            userCollection.updateOneById(user.id, user).also {
                if (!it.wasAcknowledged()) {
                    userCollection.insertOne(user)
                }
            }
        }

    }
}