package com.benjtissot.sellingmugs.controllers

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.benjtissot.sellingmugs.ConfigConst
import com.benjtissot.sellingmugs.LOGIN_PATH
import com.benjtissot.sellingmugs.REGISTER_PATH
import com.benjtissot.sellingmugs.entities.User
import com.benjtissot.sellingmugs.repositories.UserRepository
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
            if (UserRepository.authenticate(user)){
                val token = JWT.create()
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .withClaim("email", user.email)
                    .withExpiresAt(Date(System.currentTimeMillis() + 600000)) // 10 minutes
                    .sign(Algorithm.HMAC256(secret))
                // todo: set user to session
                call.sessions.set(token)
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
            // TODO: handle when user already existing tries to register
            UserRepository.insertUser(user)
            call.respond(HttpStatusCode.OK)
        }
        delete() {
            call.respond(HttpStatusCode.OK)
        }
    }
}