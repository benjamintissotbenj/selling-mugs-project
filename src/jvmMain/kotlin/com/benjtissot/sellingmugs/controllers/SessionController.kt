package com.benjtissot.sellingmugs.controllers

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.entities.Artwork
import com.benjtissot.sellingmugs.entities.Session
import com.benjtissot.sellingmugs.entities.User
import database
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

val sessionCollection = database.getCollection<Session>()
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
                // If session is found, set session user to received user
                userSession?.let{
                    val newSession = userSession.copy(user = call.receive<User>())
                    try {
                        sessionCollection.insertOne(newSession)
                        call.sessions.set(newSession)
                        call.respond(HttpStatusCode.OK)
                    } catch (e: Exception){
                        call.respond(HttpStatusCode.BadGateway)
                    }
                }
            }
        }
    }
}