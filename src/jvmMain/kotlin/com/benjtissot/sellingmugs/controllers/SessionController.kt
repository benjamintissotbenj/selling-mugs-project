package com.benjtissot.sellingmugs.controllers

import com.benjtissot.sellingmugs.SESSION_PATH
import com.benjtissot.sellingmugs.USER_PATH
import com.benjtissot.sellingmugs.entities.Session
import com.benjtissot.sellingmugs.entities.User
import com.benjtissot.sellingmugs.repositories.SessionRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import java.util.logging.Logger

fun Route.sessionRouting(){

    val LOG = Logger.getLogger(this.javaClass.name)

    route(SESSION_PATH) {
        get {
            val userSession = call.sessions.get<Session>() ?: SessionRepository.createSession()
            userSession.also {
                call.sessions.set(it)
                call.respond(it)
            }
        }
        post {
            call.respond(HttpStatusCode.OK)
        }
        delete() {
            call.respond(HttpStatusCode.OK)
        }
        route (USER_PATH) {
            post {
                val userSession = call.sessions.get<Session>()?.copy()
                LOG.info("old UserSession is $userSession")
                // If session is found, set session user to received user
                userSession?.let{
                    val updatedSession = userSession.copy(user = call.receive<User>())
                    try {
                        SessionRepository.updateSession(updatedSession)
                        call.sessions.set(updatedSession)
                        call.respond(HttpStatusCode.OK)
                    } catch (e: Exception){
                        call.respond(HttpStatusCode.BadGateway)
                    }
                }
            }
        }
    }
}