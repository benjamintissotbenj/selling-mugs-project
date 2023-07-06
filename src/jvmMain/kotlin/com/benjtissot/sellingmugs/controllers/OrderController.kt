package com.benjtissot.sellingmugs.controllers

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.entities.printify.order.Order
import com.benjtissot.sellingmugs.entities.printify.order.PushResultSerializer
import com.benjtissot.sellingmugs.entities.printify.order.StoredOrderPushFailed
import com.benjtissot.sellingmugs.repositories.OrderRepository
import com.benjtissot.sellingmugs.repositories.SessionRepository
import com.benjtissot.sellingmugs.services.OrderService
import com.benjtissot.sellingmugs.services.SessionService.Companion.getSession
import com.stripe.exception.SignatureVerificationException
import com.stripe.model.Event
import com.stripe.net.Webhook
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.util.logging.*
import kotlinx.serialization.json.Json


private val LOG = KtorSimpleLogger("OrderController.kt")

fun Route.orderRouting(){

    val endpointSecretTest = System.getenv("STRIPE_WEBHOOK_SECRET_TEST")
    val endpointSecretReal = System.getenv("STRIPE_WEBHOOK_SECRET_REAL")

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

        route("/{localOrderId}"){

            route(CANCEL_ORDER_PATH) {
                post {
                    val localOrderId: String = call.parameters["localOrderId"] ?: error("Invalid post request")

                    val httpStatusCancel = OrderService.cancelOrder(localOrderId)
                    if (httpStatusCancel != HttpStatusCode.OK) {
                        call.respond(httpStatusCancel)
                    }

                    // If cancellation happened correctly and refund too, all is good
                    call.respond(OrderService.refundOrder(localOrderId))
                }
            }

            // Used to refund an order if order was cancelled but not refunded properly
            route(REFUND_ORDER_PATH){
                post {
                    val localOrderId: String = call.parameters["localOrderId"] ?: error("Invalid post request")
                    call.respond(OrderService.refundOrder(localOrderId))
                }
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

            route(USER_ORDER_COUNT_PATH){
                get {
                    val userId: String = call.parameters["userId"] ?: error("Invalid post request")
                    OrderService.getUserOrderList(userId)?.let { userOrderList ->
                        call.respond(userOrderList.orderIds.size)
                    } ?: call.respond(HttpStatusCode.BadRequest)
                }
            }
        }


        route(PUSH_FAIL_PATH){
            get {
                if (!call.request.queryParameters["userId"].isNullOrBlank()) {
                    val userId = call.request.queryParameters["userId"]!!
                    val userOrderPushFails : List<StoredOrderPushFailed> = OrderService.getUserOrderList(userId)?.orderIds?.mapNotNull { orderId ->
                        OrderRepository.getStoredOrderPushFailByOrderId(orderId)
                    } ?: emptyList()
                    call.respond(userOrderPushFails)
                } else {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
        }

        route(PUSH_RESULT_PATH){
            get {
                if (!call.request.queryParameters["cartId"].isNullOrBlank()) {
                    val orderId = getUuidFromString(call.request.queryParameters["cartId"]!!)
                    OrderService.getOrderPushResultByOrderId(orderId)?.let {
                        // Trick to update call session from database session (needed when order goes through)
                        call.sessions.set(SessionRepository.getSession(getSession().id))
                        val pushResultString = Json.encodeToString(PushResultSerializer, it)
                        LOG.debug("Order push result found : $pushResultString")
                        call.respond(pushResultString)
                    } ?: run {
                        LOG.debug("No order push result found for orderId $orderId calculated from cartId ${call.request.queryParameters["cartId"]}")
                        call.respond(HttpStatusCode.BadRequest)
                    }
                } else if (!call.request.queryParameters["orderId"].isNullOrBlank()) {
                    val orderId = call.request.queryParameters["orderId"]!!
                    OrderService.getOrderPushResultByOrderId(orderId)?.let {
                        // Trick to update call session from database session (needed when order goes through)
                        call.sessions.set(SessionRepository.getSession(getSession().id))
                        val pushResultString = Json.encodeToString(PushResultSerializer, it)
                        LOG.debug("Order push result found : $pushResultString")
                        call.respond(pushResultString)
                    } ?: run {
                        LOG.debug("No order push result found for orderId $orderId")
                        call.respond(HttpStatusCode.BadRequest)
                    }
                } else {
                    LOG.trace("No orderId or cartId provided")
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
        }
    }


    // TODO : handle payment refused (i.e. notify user in front-end)
    route(STRIPE_WEBHOOK_PATH) {
        post {
            val payload: String = call.receiveText()
            val sigHeader: String = call.request.header("Stripe-Signature") ?: ""

            val event: Event? = OrderService.constructEvent(payload, sigHeader, endpointSecretReal)
            println("Constructed event is $event")
            val httpStatusCode = event?.let {OrderService.handleWebhookEvent(it)} ?: call.respond(HttpStatusCode.BadRequest)
            call.respond(httpStatusCode)

        }
    }

    route(STRIPE_WEBHOOK_TEST_PATH) {
        post {
            val payload: String = call.receiveText()
            val sigHeader: String = call.request.header("Stripe-Signature") ?: ""

            val event: Event? = OrderService.constructEvent(payload, sigHeader, endpointSecretTest)
            println("Constructed test event is $event")
            val httpStatusCode = event?.let {OrderService.handleWebhookEvent(it, testOrder = true)} ?: call.respond(HttpStatusCode.BadRequest)
            call.respond(httpStatusCode)

        }
    }

}