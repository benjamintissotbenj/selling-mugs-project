package com.benjtissot.sellingmugs.controllers

import com.benjtissot.sellingmugs.entities.Cart
import com.benjtissot.sellingmugs.entities.Mug
import com.benjtissot.sellingmugs.genUuid
import com.benjtissot.sellingmugs.services.CartService
import com.benjtissot.sellingmugs.services.CartService.Companion.getCart
import com.benjtissot.sellingmugs.services.SessionService.Companion.getSession
import database
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.litote.kmongo.eq

val cartCollection = database.getCollection<Cart>()
fun Route.cartRouting(){


    route(Cart.path) {
        get {
            call.respond(getCart(getSession().cartId))
        }
        post {
            cartCollection.insertOne(call.receive<Cart>().copy(genUuid().toString()))
            call.respond(HttpStatusCode.OK)
        }
        delete("/{id}") {
            val id = call.parameters["id"] ?: error("Invalid delete request")
            cartCollection.deleteOne(Cart::id eq id) //type safe
            call.respond(HttpStatusCode.OK)
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
            }
        }
    }
}