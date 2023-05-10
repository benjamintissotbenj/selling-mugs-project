package com.benjtissot.sellingmugs.controllers

import com.benjtissot.sellingmugs.HOMEPAGE_PATH
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.homepageRouting(){

    route(HOMEPAGE_PATH) {
        get {
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