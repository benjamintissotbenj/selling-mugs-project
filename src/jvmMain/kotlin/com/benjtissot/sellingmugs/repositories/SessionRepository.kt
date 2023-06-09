package com.benjtissot.sellingmugs.repositories

import com.benjtissot.sellingmugs.entities.Session
import com.benjtissot.sellingmugs.genUuid
import database
import org.litote.kmongo.upsert

val sessionCollection = database.getCollection<Session>()

class SessionRepository {
    companion object {

        /**
         * Creates a [Session] in the database
         * @return the created session
         */
        suspend fun createSession() : Session {
            // A session will always have empty clickdata and empty cart
            val clickData = ClickDataRepository.createClickData()
            val cart = CartRepository.createCart()
            val newSession = Session(genUuid(), null, null, "", clickData.id, cart.id)
            sessionCollection.insertOne(newSession)
            return newSession
        }

        /**
         * @param session the [Session] to be updated (inserted if not existent)
         */
        suspend fun updateSession(session: Session) : Session {
            sessionCollection.updateOneById(session.id, session, upsert())
            return session
        }
    }


}