package com.benjtissot.sellingmugs.controllers

import com.benjtissot.sellingmugs.CREATE_ORDER_PATH
import com.benjtissot.sellingmugs.STRIPE_WEBHOOK_PATH
import com.benjtissot.sellingmugs.entities.stripe.paramSessionId
import com.benjtissot.sellingmugs.repositories.SessionRepository
import com.benjtissot.sellingmugs.repositories.UserRepository
import com.benjtissot.sellingmugs.services.OrderService
import com.benjtissot.sellingmugs.services.SessionService.Companion.getSession
import com.google.gson.Gson
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


fun Route.orderRouting(){

    val endpointSecret = "whsec_6d89463b293d6e652ae8b5777f071f01bf96324f3965baa35db692539ea58af5"

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
                call.respond(HttpStatusCode.BadRequest)
                null
            } catch (e: Exception) {
                // Invalid payload
                call.respond(HttpStatusCode.BadRequest)
                null
            }
            event?.let {
                // Deserialize the nested object inside the event
                val dataObjectDeserializer: EventDataObjectDeserializer = event.dataObjectDeserializer
                var stripeObject: StripeObject? = null
                if (dataObjectDeserializer.getObject().isPresent) {
                    stripeObject = dataObjectDeserializer.getObject().get()
                } else {
                    call.respond(HttpStatusCode.InternalServerError)
                }
                // Handle the event
                when (event.type) {
                    "checkout.session.completed" -> {
                        println("Received webhook with event $event and stripe object $stripeObject")

                        val sType = object : TypeToken<String>() { }.type
                        // TODO: issues about how to get the data we want
                        val jsonString = stripeObject!!.toJson()

                        // We need to retrieve the sessionId this way because the webhook call does not hold the
                        // session the way the calls from the front-end do
                        val sessionId = Gson().fromJson<String>(jsonString, sType)
                        val session = SessionRepository.getSession(sessionId)
                        val user = session?.user

                    }
                    else -> println("Unhandled event type: " + event.type)
                }
            }

        }
    }

}