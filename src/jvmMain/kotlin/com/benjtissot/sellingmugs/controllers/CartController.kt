package com.benjtissot.sellingmugs.controllers

import com.benjtissot.sellingmugs.CART_PATH
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

val cartCollection = database.getCollection<Cart>()
fun Route.cartRouting(){


    route(Cart.path) {
        get {
            val cart = getCart(getSession().cartId)
            call.respond(cart)
        }
    }

    route(CART_PATH) {

        get {
            call.respondRedirect(HOMEPAGE_PATH)
        }

        // Adding a mug to the cart
        route(Mug.path){
            post {
                val mug = call.receive<Mug>().copy()
                val cart = getCart(getSession().cartId)
                try {
                    CartService.addMugToCart(mug, cart)
                } catch (e: Exception){
                    call.respond(HttpStatusCode.BadGateway)
                }
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}