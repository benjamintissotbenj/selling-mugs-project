package com.benjtissot.sellingmugs.services

import com.benjtissot.sellingmugs.entities.Session
import com.benjtissot.sellingmugs.repositories.SessionRepository
import io.ktor.server.application.*
import io.ktor.server.sessions.*
import io.ktor.util.pipeline.*

class SessionService {
    companion object {
        suspend fun PipelineContext<*, ApplicationCall>.getSession(): Session {
            // Get session from call.sessions if it exists, otherwise create it and set it to the sessions object
            return call.sessions.get() ?: SessionRepository.createSession().also { call.sessions.set(it) }
        }
    }
}