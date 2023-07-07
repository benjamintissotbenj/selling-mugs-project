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

    val endpointSecretTest = System.getenv(Const.STRIPE_WEBHOOK_SECRET_TEST_STRING)
    val endpointSecretReal = System.getenv(Const.STRIPE_WEBHOOK_SECRET_REAL_STRING)

    route(Order.path) {
        get {
            if (!call.request.queryParameters[Const.cartId].isNullOrBlank()) {
                // If we have a cartId, retrieve via cartId
                val order = OrderService.getOrderByCartId(call.request.queryParameters[Const.cartId].toString())
                if (order == null) {
                    call.respond(HttpStatusCode.BadRequest)
                } else {
                    call.respond(order)
                }
            } else {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        route("/{${Const.localOrderId}}"){

            route(CANCEL_ORDER_PATH) {
                post {
                    val localOrderId: String = call.parameters[Const.localOrderId] ?: error("Invalid post request")

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
                    val localOrderId: String = call.parameters[Const.localOrderId] ?: error("Invalid post request")
                    call.respond(OrderService.refundOrder(localOrderId))
                }
            }
        }

        route(MugCartItem.path){
            get {
                if (!call.request.queryParameters[Const.orderId].isNullOrBlank()) {
                    call.respond(OrderService.getOrderLineItemsAsMugCartItems(call.request.queryParameters[Const.orderId].toString()))
                } else {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
        }

        route("/{${Const.userId}}"){
            get {
                val userId: String = call.parameters[Const.userId] ?: error("Invalid post request")
                OrderService.getUserOrderList(userId)?.let { userOrderList ->
                    call.respond(userOrderList.orderIds.mapNotNull { OrderService.getOrderFromPrintify(it) })
                } ?: call.respond(HttpStatusCode.BadRequest)
            }

            route(USER_ORDER_COUNT_PATH){
                get {
                    val userId: String = call.parameters[Const.userId] ?: error("Invalid post request")
                    OrderService.getUserOrderList(userId)?.let { userOrderList ->
                        call.respond(userOrderList.orderIds.size)
                    } ?: call.respond(HttpStatusCode.BadRequest)
                }
            }
        }


        route(PUSH_FAIL_PATH){
            get {
                if (!call.request.queryParameters[Const.userId].isNullOrBlank()) {
                    val userId = call.request.queryParameters[Const.userId]!!
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
                val orderId = if (!call.request.queryParameters[Const.cartId].isNullOrBlank()){
                    getUuidFromString(call.request.queryParameters[Const.cartId]!!)
                } else if (!call.request.queryParameters[Const.orderId].isNullOrBlank()) {
                    call.request.queryParameters[Const.orderId]!!
                } else {
                    ""
                }

                if (orderId.isNotEmpty()){
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
            println("Constructed stripe event")
            val httpStatusCode = event?.let {OrderService.handleWebhookEvent(it)} ?: call.respond(HttpStatusCode.BadRequest)
            call.respond(httpStatusCode)

        }
    }

    route(STRIPE_WEBHOOK_TEST_PATH) {
        post {
            val payload: String = call.receiveText()
            val sigHeader: String = call.request.header("Stripe-Signature") ?: ""

            val event: Event? = OrderService.constructEvent(payload, sigHeader, endpointSecretTest)
            println("Constructed test stripe event")
            val httpStatusCode = event?.let {OrderService.handleWebhookEvent(it, testOrder = true)} ?: call.respond(HttpStatusCode.BadRequest)
            call.respond(httpStatusCode)

        }
    }

}