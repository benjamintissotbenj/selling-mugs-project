package com.benjtissot.sellingmugs.controllers

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.entities.printify.order.Order
import com.benjtissot.sellingmugs.entities.printify.order.PushResultSerializer
import com.benjtissot.sellingmugs.entities.printify.order.UserOrderList
import com.benjtissot.sellingmugs.entities.stripe.paramSessionId
import com.benjtissot.sellingmugs.repositories.CartRepository
import com.benjtissot.sellingmugs.repositories.SessionRepository
import com.benjtissot.sellingmugs.repositories.UserRepository
import com.benjtissot.sellingmugs.services.OrderService
import com.benjtissot.sellingmugs.services.SessionService.Companion.getSession
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.stripe.exception.SignatureVerificationException
import com.stripe.model.Event
import com.stripe.model.EventDataObjectDeserializer
import com.stripe.model.StripeObject
import com.stripe.net.Webhook
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.serialization.json.Json


fun Route.orderRouting(){

    val endpointSecret = "whsec_6d89463b293d6e652ae8b5777f071f01bf96324f3965baa35db692539ea58af5"
    route(Order.path) {
        get {
            if (!call.request.queryParameters["cartId"].isNullOrBlank()) {
                // If we have a cartId, retrieve via cartId
                val order = OrderService.getOrderByCartId(call.request.queryParameters["cartId"].toString())
                if (order == null) {
                    call.respond(HttpStatusCode.BadRequest)
                } else {
                    call.respond(order)
                }
            } else {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        route("/{localOrderId}$CANCEL_ORDER_PATH"){
            get {
                val localOrderId: String = call.parameters["localOrderId"] ?: error("Invalid post request")
                call.respond(OrderService.cancelOrder(localOrderId))
            }
        }

        route(MugCartItem.path){
            get {
                if (!call.request.queryParameters["orderId"].isNullOrBlank()) {
                    call.respond(OrderService.getOrderLineItemsAsMugCartItems(call.request.queryParameters["orderId"].toString()))
                } else {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
        }

        route("/{userId}"){
            get {
                val userId: String = call.parameters["userId"] ?: error("Invalid post request")
                OrderService.getUserOrderList(userId)?.let { userOrderList ->
                    call.respond(userOrderList.orderIds.mapNotNull { OrderService.getOrderFromPrintify(it) })
                } ?: call.respond(HttpStatusCode.BadRequest)
            }
        }

        route(PUSH_RESULT_PATH){
            get {
                if (!call.request.queryParameters["cartId"].isNullOrBlank()) {
                    val orderId = getUuidFromString(call.request.queryParameters["cartId"]!!)
                    OrderService.getOrderPushResultByOrderId(orderId)?.let {
                        // Trick to update call session from database session (needed when order goes through)
                        call.sessions.set(SessionRepository.getSession(getSession().id))

                        call.respond(Json.encodeToString(PushResultSerializer, it))
                    } ?: call.respond(HttpStatusCode.BadRequest)
                } else {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
        }

        route(CREATE_ORDER_PATH) {

            post {
                val session = getSession()
                if (session.user == null) {
                    // TODO: at some point, replace all those errors with custom HttpStatus Codes
                    call.respond(HttpStatusCode.InternalServerError)
                }
                val order = OrderService.createOrderFromCart(call.receive(), session.cartId, session.user!!)

                call.sessions.set(SessionRepository.updateSession(
                    session.copy(orderId = order.external_id, cartId = CartRepository.createCart().id)
                )) // update session with orderId and updated user

                call.respond(order)
            }
        }
    }

    route(STRIPE_WEBHOOK_PATH) {
        post {
            val payload: String = call.receiveText()
            val sigHeader: String = call.request.header("Stripe-Signature") ?: ""

            val event: Event? = try {
                Webhook.constructEvent(
                    payload, sigHeader, endpointSecret
                )
            } catch (e: SignatureVerificationException) {
                // Invalid signature
                println("Signature Verification Exception")
                null
            } catch (e: Exception) {
                // Invalid payload
                println("Invalid Payload")
                null
            }
            println("Constructed event is $event")
            val httpStatusCode = event?.let {OrderService.handleWebhookEvent(it)} ?: call.respond(HttpStatusCode.BadRequest)
            call.respond(httpStatusCode)

        }
    }

}