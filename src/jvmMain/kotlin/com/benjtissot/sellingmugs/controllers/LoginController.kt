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
import io.ktor.server.config.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import java.util.*

val LOG = java.util.logging.Logger.getLogger("LoginController.kt")
fun Route.loginRouting(){

    route(LOGIN_PATH) {
        get {
            call.respond(HttpStatusCode.OK)
        }
        post {
            login(call.receive())
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
            register(call.receive())
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