package com.benjtissot.sellingmugs.controllers

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.entities.Artwork
import com.benjtissot.sellingmugs.entities.Session
import com.benjtissot.sellingmugs.entities.User
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

fun Route.sessionRouting(){

    // TODO: deal with this at some point

    route(SESSION_PATH) {
        get {
            val userSession = call.sessions.get<Session>()
            userSession?.let { call.respond(userSession)} ?: call.respond(HttpStatusCode.BadRequest)
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
                userSession?.let{call.sessions.set(userSession.copy(user = call.receive<User>()))}
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}