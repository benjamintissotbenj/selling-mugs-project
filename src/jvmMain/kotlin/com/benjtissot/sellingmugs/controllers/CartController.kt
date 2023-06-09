package com.benjtissot.sellingmugs.controllers

import com.benjtissot.sellingmugs.CART_PATH
import com.benjtissot.sellingmugs.MugCartItem
import com.benjtissot.sellingmugs.entities.Cart
import com.benjtissot.sellingmugs.entities.Mug
import com.benjtissot.sellingmugs.services.CartService
import com.benjtissot.sellingmugs.services.CartService.Companion.getCart
import com.benjtissot.sellingmugs.services.SessionService.Companion.getSession
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.cartRouting(){


    route(Cart.path) {
        get {
            getCart(getSession().cartId)?.let {
                call.respond(it)
            } ?: let {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }
    }

    route(CART_PATH) {

        // Adding a mug to the cart
        route(Mug.path){
            post {
                val mug = call.receive<Mug>().copy()
                getCart(getSession().cartId)?.let {
                    try {
                        CartService.addMugToCart(mug, it)
                    } catch (e: Exception){
                        call.respond(HttpStatusCode.BadGateway)
                    }
                    call.respond(HttpStatusCode.OK)
                } ?: let {
                    call.respond(HttpStatusCode.InternalServerError)
                }

            }
        }

        route(MugCartItem.path){
            delete{
                val mugCartItem = call.receive<MugCartItem>().copy()
                getCart(getSession().cartId)?.let {
                    try {
                        CartService.removeMugCartItemFromCart(mugCartItem, it)
                    } catch (e: Exception){
                        call.respond(HttpStatusCode.BadGateway)
                    }
                    call.respond(HttpStatusCode.OK)
                } ?: let {
                    call.respond(HttpStatusCode.InternalServerError)
                }

            }
        }
    }
}