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
            val clickData = ClickDataRepository.createClickData()
            val newSession = Session(genUuid(), null, "", clickData.id, "")
            sessionCollection.insertOne(newSession)
            return newSession
        }

        /**
         * @param session the [Session] to be updated (inserted if not existent)
         */
        suspend fun updateSession(session: Session) {
            sessionCollection.updateOneById(session.id, session, upsert())
        }
    }


}