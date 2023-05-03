package com.benjtissot.sellingmugs.controllers

import com.benjtissot.sellingmugs.CHECKOUT_PATH
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.checkoutRouting(){

    route(CHECKOUT_PATH) {
        get {
            call.respond("Hello Checkout Controller")
        }
        post {
            call.respond(HttpStatusCode.OK)
        }
        delete() {
            call.respond(HttpStatusCode.OK)
        }
    }
}