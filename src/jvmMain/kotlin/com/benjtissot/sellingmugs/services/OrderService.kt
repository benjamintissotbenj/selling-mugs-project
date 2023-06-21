package com.benjtissot.sellingmugs.services

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.entities.User
import com.benjtissot.sellingmugs.entities.printify.order.*
import com.benjtissot.sellingmugs.repositories.OrderRepository
import com.benjtissot.sellingmugs.repositories.OrderRepository.Companion.getOrderPrintifyId
import com.benjtissot.sellingmugs.repositories.SessionRepository
import com.benjtissot.sellingmugs.repositories.UserRepository
import com.stripe.model.Event
import com.stripe.model.EventDataObjectDeserializer
import com.stripe.model.StripeObject
import com.stripe.model.checkout.Session
import com.stripe.model.checkout.Session.CustomerDetails
import io.ktor.client.call.*
import io.ktor.http.*

class OrderService {
    companion object {

        suspend fun getOrder(id: String) : Order? {
            return OrderRepository.getOrder(id)
        }

        /**
         * Gets an order from Printify
         * @param localId the local id of the Order we want to get
         * @return the [Order]
         */
        suspend fun getOrderFromPrintify(localId: String) : Order? {
            val httpResponse = apiGetOrder(getOrderPrintifyId(localId))
            return if (httpResponse.status == HttpStatusCode.OK){
                val receiveOrder = httpResponse.body<ReceiveOrder>()
                OrderRepository.getOrder(localId)
                    ?.let { OrderRepository.updateOrder(it.copy(status = receiveOrder.status)) }
                OrderRepository.getOrderByPrintifyId(receiveOrder.id) // Printify id
            } else {
                null
            }
        }

        /**
         * Creates an order and populates it with the correct information of what is in the cart
         * /!\ Don't forget to update session after creating the Order
         *
         * @param addressTo the address to which deliver the order
         * @param cartId the id of the cart to populate the order with
         * @return the [Order] created and added to the database
         */
        suspend fun createOrderFromCart(addressTo: AddressTo, cartId: String, user: User) : Order {
            val cart = CartService.getCart(cartId)
            val lineItems: ArrayList<LineItem> = ArrayList(emptyList())
            cart?.let {lineItems.addAll(cart.mugCartItemList.map {LineItem(it.mug.printifyId, it.amount, 69010)})}
            val newOrder = Order.create(genUuid(), OrderRepository.getOrderNextLabel(), lineItems, addressTo)

            // Insert order in database
            OrderRepository.insertOrder(newOrder)

            // Put orderId in user
            UserRepository.updateUser(user.addOrderId(newOrder.external_id))

            return newOrder
        }

        /**
         * Calculates the shipping costs for an order from Printify
         * @param orderId the id of the order for which we want to calculate shipping costs
         * @return a [ShippingCosts] object that holds the different shipping costs (standard/express)
         */
        suspend fun calculateOrderShippingCost(orderId: String) : ShippingCosts? {
            val order = getOrder(orderId) ?: return null
            val httpResponse = apiCalculateOrderShippingCost(order.getCalculateShipping())
            return if (httpResponse.status == HttpStatusCode.OK){
                httpResponse.body<ShippingCosts>()
            } else {
                null
            }

        }


        /**
         * Places an order to Printify. In case of success, updates the "id" field of the order in the database
         * @param orderLocalId the local id of the order to place to Printify
         * @return a [PrintifyOrderPushResult] object that holds all the information concerning the status of the
         * push order if it fails, and the printify ID if it is a success.
         */
        suspend fun placeOrderToPrintify(orderLocalId: String) : PrintifyOrderPushResult {
            val order = getOrder(orderLocalId) ?: return PrintifyOrderPushFail.notFoundInDatabase
            return when (val printifyOrderPushResult = apiPlaceOrder(order)) {
                is PrintifyOrderPushFail -> {
                    printifyOrderPushResult
                }
                is PrintifyOrderPushSuccess -> {
                    // Get printifyId and store it in Order Object
                    OrderRepository.updateOrder(order.copy(id = printifyOrderPushResult.id))
                    printifyOrderPushResult
                }
                else -> {
                    PrintifyOrderPushFail.notFoundInDatabase
                }
            }
        }

        /**
         * Cancels a specific order from Printify
         * @param orderId the local id of the order to be cancelled
         * @return a [HttpStatusCode] that tells us if the order was cancelled
         */
        suspend fun cancelOrder(orderId: String) : HttpStatusCode {
            val printifyId = getOrderPrintifyId(orderId)
            return if (printifyId.isEmpty()){
                HttpStatusCode(6, "Order was not found in database")
            } else {
                apiCancelOrder(getOrderPrintifyId(orderId))
            }
        }


        /****************************************************************
         *
         *                      Webhook handling
         *
         ****************************************************************/

        /**
         * Handles a Stripe webhook [Event]
         * @return a [HttpStatusCode] to confirm there were no errors
         */
        suspend fun handleWebhookEvent(event: Event) : HttpStatusCode {
            // Deserialize the nested object inside the event
            val dataObjectDeserializer: EventDataObjectDeserializer = event.dataObjectDeserializer
            if (!dataObjectDeserializer.getObject().isPresent) {
                return HttpStatusCode.InternalServerError
            }

            val stripeObject = dataObjectDeserializer.getObject().get()
            // Handle the event
            return when (event.type) {
                "checkout.session.completed" -> {
                    println("Received webhook with event ${event.type} and stripe object $stripeObject")
                    // Getting necessary information from the strip object sent
                    handleCheckoutSessionCompleted(stripeObject as Session)
                }

                else -> {
                    println("Unhandled event type: ${event.type}")
                    HttpStatusCode.OK
                }
            }
        }

        private suspend fun handleCheckoutSessionCompleted(stripeSession: Session) : HttpStatusCode {
            val sessionId : String? = stripeSession.clientReferenceId
            val session = SessionRepository.getSession(sessionId ?: "") ?: return HttpStatusCode.InternalServerError
            val user = session.user ?: return HttpStatusCode.InternalServerError
            val addressTo = stripeSession.customerDetails.toAddressTo()

            // Create order
            val order = createOrderFromCart(addressTo, session.cartId, user)
            SessionRepository.updateSession(
                session.copy(orderId = order.external_id, user = UserRepository.getUserById(user.id))
            )

            // Push order to printify
            // TODO: in case of error, notify the user in the front-end of an error in the address
            val pushResult = placeOrderToPrintify(order.external_id)
            if (pushResult is PrintifyOrderPushSuccess){
                print("Order ${pushResult.id} pushed successfully")
            } else if (pushResult is PrintifyOrderPushFail) {
                print(pushResult.message)
            }
            return HttpStatusCode.OK
        }

    }
}

/**
 * Creates an [AddressTo] object from Stripe's [CustomerDetails]
 */
fun CustomerDetails.toAddressTo() : AddressTo {
    val split = this.name.split(" ")

    val lastName = if (split.size <= 1) "" else split[split.size-1]
    val firstName = if (split.size <= 1) this.name else split.subList(0, split.size - 1).joinToString(" ")
    return AddressTo(
        firstName,
        lastName,
        this.email,
        this.phone ?: "",
        this.address.country,
        "England",
        this.address.line1,
        this.address.line2 ?: "",
        this.address.city,
        this.address.postalCode
    )
}