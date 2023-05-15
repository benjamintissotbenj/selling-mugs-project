package com.benjtissot.sellingmugs.controllers

import com.benjtissot.sellingmugs.SESSION_OBJECT_PATH
import com.benjtissot.sellingmugs.USER_OBJECT_PATH
import com.benjtissot.sellingmugs.services.SessionService.Companion.getSession
import com.benjtissot.sellingmugs.services.SessionService.Companion.updateUserInSession
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.logging.Logger

fun Route.sessionRouting(){

    val LOG = Logger.getLogger(this.javaClass.name)

    route(SESSION_OBJECT_PATH) {
        get {
            getSession()
        }
        post {
            call.respond(HttpStatusCode.OK)
        }
        delete() {
            call.respond(HttpStatusCode.OK)
        }
    }
}