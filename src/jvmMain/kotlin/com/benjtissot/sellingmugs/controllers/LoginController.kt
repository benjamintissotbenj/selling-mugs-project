package com.benjtissot.sellingmugs.controllers

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.entities.Session
import com.benjtissot.sellingmugs.entities.User
import com.benjtissot.sellingmugs.repositories.SessionRepository
import com.benjtissot.sellingmugs.repositories.UserRepository
import com.benjtissot.sellingmugs.repositories.UserRepository.Companion.getUserByEmail
import com.typesafe.config.ConfigFactory
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import java.util.*

val LOG = java.util.logging.Logger.getLogger("LoginController.kt")
fun Route.loginRouting(){

    val appConfig = HoconApplicationConfig(ConfigFactory.load())
    val secret = ConfigConst.SECRET
    val issuer = ConfigConst.ISSUER
    val audience = ConfigConst.AUDIENCE
    val myRealm = ConfigConst.REALM

    route(LOGIN_PATH) {
        get {
            call.respond(HttpStatusCode.OK)
        }
        post {
            val user = call.receive<User>()
            LOG.info("User is $user is authenticated : ${UserRepository.authenticate(user)}")
            val authenticatedUser = UserRepository.authenticate(user)

            if (authenticatedUser != null){
                val token = JWT.create()
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .withClaim("email", user.email)
                    .withExpiresAt(Date(System.currentTimeMillis() + 600000)) // 10 minutes
                    .sign(Algorithm.HMAC256(secret))

                // Setting the logged in user to authenticatedUser and jwt to token
                val userSession = call.sessions.get<Session>()?.copy()

                // If session is found, set session user to received user
                userSession?.let{
                    val updatedSession = userSession.copy(user = authenticatedUser, jwtToken = token)
                    try {
                        SessionRepository.updateSession(updatedSession)
                        call.sessions.set(updatedSession)
                    } catch (e: Exception){
                        call.respond(HttpStatusCode.BadGateway)
                    }
                }
                call.respond(token)
            } else {
                call.respondRedirect(LOGIN_PATH)
            }


        }
        delete() {
            call.respond(HttpStatusCode.OK)
        }
    }

    route(REGISTER_PATH) {
        get {
            call.respond("Hello Register Controller")
        }
        post {
            val user = call.receive<User>()

            getUserByEmail(user.email)?.let{
                // If user is found, error and cannot register new user
                LOG.severe("User with email ${user.email} already exists, sending Conflict")
                call.respond(HttpStatusCode.Conflict)
            } ?: let {
                // If user is not found, insert with new UUID
                UserRepository.insertUser(user.copy(id = genUuid().toString()))

                LOG.info("User is $user is authenticated : ${UserRepository.authenticate(user)}")
                val authenticatedUser = UserRepository.authenticate(user)

                if (authenticatedUser != null){
                    val token = JWT.create()
                        .withAudience(audience)
                        .withIssuer(issuer)
                        .withClaim("email", user.email)
                        .withExpiresAt(Date(System.currentTimeMillis() + 600000)) // 10 minutes
                        .sign(Algorithm.HMAC256(secret))

                    // Setting the logged in user to authenticatedUser and jwt to token
                    val userSession = call.sessions.get<Session>()?.copy()

                    // If session is found, set session user to received user
                    userSession?.let{
                        val updatedSession = userSession.copy(user = authenticatedUser, jwtToken = token)
                        try {
                            SessionRepository.updateSession(updatedSession)
                            call.sessions.set(updatedSession)
                        } catch (e: Exception){
                            call.respond(HttpStatusCode.BadGateway)
                        }
                    }
                    call.respond(token)
                } else {
                    call.respondRedirect(LOGIN_PATH)
                }
            }


        }
        delete() {
            call.respond(HttpStatusCode.OK)
        }
    }

    route(LOGOUT_PATH) {
        get {
            // Setting the jwt to ""
            val userSession = call.sessions.get<Session>()?.copy()

            // If session is found, keep session user in database but delete jwt
            userSession?.let{
                val updatedSession = userSession.copy(jwtToken = "")
                try {
                    SessionRepository.updateSession(updatedSession)
                    call.sessions.set(updatedSession)
                } catch (e: Exception){
                    call.respond(HttpStatusCode.BadGateway)
                }
            }
            call.respond(HttpStatusCode.OK)
        }
        post {
            call.respond(HttpStatusCode.OK)
        }
        delete() {
            call.respond(HttpStatusCode.OK)
        }
    }
}