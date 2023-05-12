package com.benjtissot.sellingmugs.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.benjtissot.sellingmugs.ConfigConst
import com.benjtissot.sellingmugs.LOGIN_PATH
import com.benjtissot.sellingmugs.controllers.LOG
import com.benjtissot.sellingmugs.entities.Session
import com.benjtissot.sellingmugs.entities.User
import com.benjtissot.sellingmugs.genUuid
import com.benjtissot.sellingmugs.repositories.SessionRepository
import com.benjtissot.sellingmugs.repositories.UserRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import io.ktor.util.pipeline.*
import java.util.*

class LoginService {
    companion object {
        val secret = ConfigConst.SECRET
        val issuer = ConfigConst.ISSUER
        val audience = ConfigConst.AUDIENCE
        val myRealm = ConfigConst.REALM

        suspend fun PipelineContext<*, ApplicationCall>.login(){
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
                    try {
                        userSession.copy(user = authenticatedUser, jwtToken = token).also{
                            SessionRepository.updateSession(it)
                            call.sessions.set(it)
                        }
                        call.respond(token)
                    } catch (e: Exception){
                        // If session cannot be updated
                        call.respond(HttpStatusCode.BadGateway)
                    }
                } ?: run {
                    // If session is not found, return Bad Gateway
                    call.respond(HttpStatusCode.BadGateway)
                }
            } else {
                call.respond(HttpStatusCode.Conflict)
            }
        }

        suspend fun PipelineContext<*, ApplicationCall>.register(){
            val user = call.receive<User>()

            UserRepository.getUserByEmail(user.email)?.let{
                // If user is found, error and cannot register new user
                LOG.severe("User with email ${user.email} already exists, sending Conflict")
                call.respond(HttpStatusCode.Conflict)
            } ?: let {
                // If user is not found, insert with new UUID
                user.copy(id = genUuid().toString()).also {
                    UserRepository.insertUser(it)
                    login()
                }
            }
        }

        suspend fun PipelineContext<*, ApplicationCall>.logout(){
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
    }
}