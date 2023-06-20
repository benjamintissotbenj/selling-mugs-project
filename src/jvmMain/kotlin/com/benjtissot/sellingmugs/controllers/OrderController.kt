package com.benjtissot.sellingmugs.controllers

import com.benjtissot.sellingmugs.CART_PATH
import com.benjtissot.sellingmugs.CREATE_ORDER_PATH
import com.benjtissot.sellingmugs.MugCartItem
import com.benjtissot.sellingmugs.entities.Cart
import com.benjtissot.sellingmugs.entities.Mug
import com.benjtissot.sellingmugs.entities.printify.order.Order
import com.benjtissot.sellingmugs.repositories.SessionRepository
import com.benjtissot.sellingmugs.repositories.UserRepository
import com.benjtissot.sellingmugs.services.CartService
import com.benjtissot.sellingmugs.services.CartService.Companion.getCart
import com.benjtissot.sellingmugs.services.OrderService
import com.benjtissot.sellingmugs.services.SessionService.Companion.getSession
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

fun Route.orderRouting(){

    route(CREATE_ORDER_PATH) {
        post {
            val session = getSession()
            if (session.user == null) {
                // TODO: at some point, replace all those errors with custom HttpStatus Codes
                call.respond(HttpStatusCode.InternalServerError)
            }
            val order = OrderService.createOrderFromCart(call.receive(), session.cartId, session.user!!)

            call.sessions.set(SessionRepository.updateSession(
                session.copy(orderId = order.external_id, user = UserRepository.getUserById(session.user!!.id))
            )) // update session with orderId and updated user

            call.respond(order)
        }
    }
}