package com.benjtissot.sellingmugs.controllers

import com.benjtissot.sellingmugs.CHECK_REDIRECT_PATH
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.logging.*
import redirectPath

private val LOG = KtorSimpleLogger("CheckRedirectController.kt")
fun Route.checkRedirectRouting(){


    route(CHECK_REDIRECT_PATH) {
        post {
            LOG.info("Redirect path is $redirectPath")
            call.respond(redirectPath)
            redirectPath = ""   // we only need to redirect once, delete redirect after first request
        }
    }

}