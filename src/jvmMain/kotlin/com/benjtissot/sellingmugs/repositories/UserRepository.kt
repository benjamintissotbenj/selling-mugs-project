package com.benjtissot.sellingmugs.repositories

import com.benjtissot.sellingmugs.entities.User
import database
import org.litote.kmongo.and
import org.litote.kmongo.eq

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

        /**
         * @param user the [User] to be authenticated
         * @return a [Boolean] determining if the user is authenticated
         */
        suspend fun authenticate(user: User) : Boolean {
            return userCollection.findOne(and((User::email eq user.id), User::passwordHash eq user.passwordHash)) != null
        }

    }
}