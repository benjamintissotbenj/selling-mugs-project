package com.benjtissot.sellingmugs.controllers

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.entities.local.Cart
import com.benjtissot.sellingmugs.entities.local.Mug
import com.benjtissot.sellingmugs.entities.local.MugCartItem
import com.benjtissot.sellingmugs.repositories.UserRepository
import com.benjtissot.sellingmugs.services.CartService
import com.benjtissot.sellingmugs.services.CartService.Companion.getCart
import com.benjtissot.sellingmugs.services.CartService.Companion.loadCartIdIntoSession
import com.benjtissot.sellingmugs.services.SessionService
import com.benjtissot.sellingmugs.services.SessionService.Companion.addItemsToCartCount
import com.benjtissot.sellingmugs.services.SessionService.Companion.getSession
import com.benjtissot.sellingmugs.services.SessionService.Companion.removeItemToCartCount
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
                call.respond(getCart(getSession()))
            }
        }
        route("$CART_SAVE_TO_USER_PATH/{${Const.userId}}"){
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
                val loadCartResult = loadCartIdIntoSession(getSession())
                if (loadCartResult.first){
                    call.sessions.set(loadCartResult.second)
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
                try {
                    val updatedSession = getSession().addItemsToCartCount(1)
                    call.sessions.set(updatedSession)
                    CartService.addMugToCart(mug, getCart(updatedSession))
                } catch (e: Exception){
                    call.respond(HttpStatusCode.BadGateway)
                }
                call.respond(HttpStatusCode.OK)
            }
        }

        route(MugCartItem.path){
            delete{
                val mugCartItem = call.receive<MugCartItem>().copy()
                try {
                    val updatedSession = getSession().removeItemToCartCount(mugCartItem.amount)
                    call.sessions.set(updatedSession)
                    CartService.removeMugCartItemFromCart(mugCartItem, getCart(updatedSession))
                } catch (e: Exception){
                    call.respond(HttpStatusCode.BadGateway)
                }
                call.respond(HttpStatusCode.OK)
            }

            post {
                val mugCartItem = call.receive<MugCartItem>().copy()
                val deltaQuantity: Int = call.parameters[Const.deltaQuantity]?.toInt() ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                    0
                }
                try {
                    val updatedSession = getSession().addItemsToCartCount(deltaQuantity)
                    call.sessions.set(updatedSession)
                    CartService.changeMugCartItemQuantity(getCart(getSession()), mugCartItem, deltaQuantity)
                } catch (e: Exception){
                    call.respond(HttpStatusCode.BadGateway)
                }
                call.respond(HttpStatusCode.OK)

            }
        }
    }
}