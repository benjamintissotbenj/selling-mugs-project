package com.benjtissot.sellingmugs.controllers

import com.benjtissot.sellingmugs.SESSION_PATH
import com.benjtissot.sellingmugs.USER_INFO_PATH
import com.benjtissot.sellingmugs.USER_PATH
import com.benjtissot.sellingmugs.entities.Session
import com.benjtissot.sellingmugs.entities.User
import com.benjtissot.sellingmugs.repositories.SessionRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import java.util.logging.Logger

fun Route.userInfoRouting(){

    val LOG = Logger.getLogger(this.javaClass.name)

    route(USER_INFO_PATH) {
        authenticate("auth-jwt") {
            get {
                val principal = call.principal<JWTPrincipal>()
                val email = principal!!.payload.getClaim("email").asString()
                val expiresAt = principal.expiresAt?.time?.minus(System.currentTimeMillis())
                call.respondText("Hello, $email! Token is expired at $expiresAt ms.")
            }
            post {
                call.respond(HttpStatusCode.OK)
            }
            delete() {
                call.respond(HttpStatusCode.OK)
            }
        }

    }
}