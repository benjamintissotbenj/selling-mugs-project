package com.benjtissot.sellingmugs.repositories

import com.benjtissot.sellingmugs.entities.User
import database
import org.litote.kmongo.MongoOperator
import org.litote.kmongo.and
import org.litote.kmongo.eq
import org.litote.kmongo.upsert

val userCollection = database.getCollection<User>()

class UserRepository {
    companion object {

        /**
         * @param user the [User] to be inserted
         */
        suspend fun getUserList() : List<User> {
            return userCollection.find().toList()
        }

        /**
         * @param user the [User] to be inserted
         */
        suspend fun insertUser(user: User) {
            userCollection.insertOne(user)
        }

        /**
         * @param user the [User] to be inserted
         */
        suspend fun getUserByEmail(email: String): User? {
            return userCollection.findOne(User::email eq email)
        }

        /**
         * @param user the [User] to be updated (inserted if not existent)
         */
        suspend fun updateUser(user: User) {
            userCollection.updateOneById(user.id, user, upsert())
        }

        /**
         * @param user the [User] to be authenticated
         * @return a [Boolean] determining if the user is authenticated
         */
        suspend fun authenticate(user: User) : User? {
            return userCollection.findOne(and(User::email eq user.email, User::passwordHash eq user.passwordHash))
        }

    }
}