package com.benjtissot.sellingmugs.controllers

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.entities.Session
import com.benjtissot.sellingmugs.entities.User
import com.benjtissot.sellingmugs.repositories.SessionRepository
import com.benjtissot.sellingmugs.repositories.UserRepository
import com.benjtissot.sellingmugs.repositories.UserRepository.Companion.getUserByEmail
import com.benjtissot.sellingmugs.services.LoginService.Companion.login
import com.benjtissot.sellingmugs.services.LoginService.Companion.logout
import com.benjtissot.sellingmugs.services.LoginService.Companion.register
import com.typesafe.config.ConfigFactory
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.config.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import java.util.*

val LOG = java.util.logging.Logger.getLogger("LoginController.kt")
fun Route.loginRouting(){

    route(LOGIN_BACKEND_PATH) {
        authenticate("auth-jwt") {
            get {
                // Returns a boolean whether or not the user is still logged in
                val principal = call.principal<JWTPrincipal>()
                call.respond((principal?.expiresAt?.after(Date(System.currentTimeMillis())) ?: false).toString())
            }
        }
        post {
            login()
        }
        delete() {
            call.respond(HttpStatusCode.OK)
        }
    }

    route(REGISTER_PATH) {
        post {
            register()
        }
        delete() {
            call.respond(HttpStatusCode.OK)
        }
    }

    route(LOGOUT_PATH) {
        get {
            logout()
        }
        post {
            call.respond(HttpStatusCode.OK)
        }
        delete() {
            call.respond(HttpStatusCode.OK)
        }
    }
}