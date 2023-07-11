package com.benjtissot.sellingmugs.controllers

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.entities.Cart
import com.benjtissot.sellingmugs.entities.Mug
import com.benjtissot.sellingmugs.repositories.SessionRepository
import com.benjtissot.sellingmugs.repositories.UserRepository
import com.benjtissot.sellingmugs.services.CartService
import com.benjtissot.sellingmugs.services.CartService.Companion.getCart
import com.benjtissot.sellingmugs.services.CartService.Companion.loadCartIdIntoSession
import com.benjtissot.sellingmugs.services.SessionService.Companion.getSession
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.util.logging.*

fun Route.cartRouting(){

    val LOG = KtorSimpleLogger("CartController.kt")

    route(Cart.path) {
        get {
            if (!call.request.queryParameters[Const.userId].isNullOrBlank()){
                val userId = call.request.queryParameters[Const.userId] ?: ""
                LOG.debug("Fetching cart for user : $userId")
                UserRepository.getUserById(userId)?.let { user ->
                    getCart(user.savedCartId)?.let {call.respond(it)} ?: run {
                        LOG.debug("No saved cart found for user $userId")
                        call.respond(HttpStatusCode.InternalServerError)
                    }
                } ?: run {
                    LOG.debug("Could not find user $userId")
                    call.respond(HttpStatusCode.InternalServerError)
                }
            } else {
                getCart(getSession().cartId)?.let {
                    call.respond(it)
                } ?: let {
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }
        }
        route("$CART_SAVE_TO_USER_PATH/{${Const.userId}}"){ // todo: handle this without userId in the path
            post {
                val userId = call.parameters[Const.userId]
                userId?.let {
                    val updatedSession = CartService.saveCartToUser(getSession(), it)
                    updatedSession?.let { session ->
                        call.sessions.set(session)
                        call.respond(HttpStatusCode.OK)
                    } ?: run {
                        call.respond(HttpStatusCode.InternalServerError)
                    }
                } ?: let {
                    LOG.error("No userId was found")
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
        }

        route(CART_LOAD_FROM_USER_PATH){
            post {
                // Loads the saved cartId into the session
                if (loadCartIdIntoSession(getSession())){
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.InternalServerError)
                }
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