package com.benjtissot.sellingmugs.controllers

import com.benjtissot.sellingmugs.HOMEPAGE_PATH
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.homepageRouting(){

    route(HOMEPAGE_PATH) {
        get {
            call.respond("Hello Home Controller")
        }
        post {
            call.respond(HttpStatusCode.OK)
        }
        delete() {
            call.respond(HttpStatusCode.OK)
        }
    }
}