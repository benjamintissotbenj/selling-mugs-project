package com.benjtissot.sellingmugs.controllers

import com.benjtissot.sellingmugs.LOGIN_PATH
import com.benjtissot.sellingmugs.REGISTER_PATH
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.loginRouting(){

    // TODO: deal with this at some point

    route(LOGIN_PATH) {
        get {
            call.respond("Hello Login Controller")
        }
        post {
            call.respond(HttpStatusCode.OK)
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
            call.respond(HttpStatusCode.OK)
        }
        delete() {
            call.respond(HttpStatusCode.OK)
        }
    }
}