package com.benjtissot.sellingmugs.controllers

import com.benjtissot.sellingmugs.CART_PATH
import com.benjtissot.sellingmugs.CHECK_REDIRECT_PATH
import com.benjtissot.sellingmugs.HOMEPAGE_PATH
import com.benjtissot.sellingmugs.entities.Cart
import com.benjtissot.sellingmugs.entities.Mug
import com.benjtissot.sellingmugs.services.CartService
import com.benjtissot.sellingmugs.services.CartService.Companion.getCart
import com.benjtissot.sellingmugs.services.SessionService.Companion.getSession
import database
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import redirectPath

fun Route.checkRedirectRouting(){


    route(CHECK_REDIRECT_PATH) {
        post {
            LOG.info("Redirect path is $redirectPath")
            call.respond(redirectPath)
            redirectPath = ""   // we only need to redirect once, delete redirect after first request
        }
    }

}