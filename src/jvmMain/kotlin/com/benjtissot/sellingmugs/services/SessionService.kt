package com.benjtissot.sellingmugs.services

import com.benjtissot.sellingmugs.entities.Session
import com.benjtissot.sellingmugs.repositories.SessionRepository
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import io.ktor.util.pipeline.*

class SessionService {
    companion object {
        suspend fun PipelineContext<*, ApplicationCall>.getSession(): Session{
            val userSession = call.sessions.get<Session>() ?: SessionRepository.createSession()
            userSession.also {
                call.sessions.set(it)
                call.respond(it)
                return it
            }
        }

        suspend fun PipelineContext<*, ApplicationCall>.updateCartIdInSession(cartId: String){
            getSession().copy(cartId = cartId).also {
                call.sessions.set(it)
            }
        }
    }
}