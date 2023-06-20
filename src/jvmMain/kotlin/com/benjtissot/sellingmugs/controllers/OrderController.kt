package com.benjtissot.sellingmugs.controllers

import com.benjtissot.sellingmugs.CART_PATH
import com.benjtissot.sellingmugs.MugCartItem
import com.benjtissot.sellingmugs.entities.Cart
import com.benjtissot.sellingmugs.entities.Mug
import com.benjtissot.sellingmugs.entities.printify.order.Order
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

    route(Order.path) {
        get {
            TODO("implement")
            //OrderService.createOrderFromCart()

            //call.sessions.set() update session with orderId and updated user
        }
    }
}