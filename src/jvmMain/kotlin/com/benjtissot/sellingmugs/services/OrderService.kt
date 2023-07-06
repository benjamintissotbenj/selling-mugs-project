package com.benjtissot.sellingmugs.services

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.entities.User
import com.benjtissot.sellingmugs.entities.printify.order.*
import com.benjtissot.sellingmugs.repositories.*
import com.benjtissot.sellingmugs.repositories.OrderRepository.Companion.getOrderPrintifyId
import com.stripe.Stripe
import com.stripe.exception.SignatureVerificationException
import com.stripe.model.Event
import com.stripe.model.EventDataObjectDeserializer
import com.stripe.model.Refund
import com.stripe.model.checkout.Session
import com.stripe.model.checkout.Session.CustomerDetails
import com.stripe.net.Webhook
import io.ktor.client.call.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.logging.*

private val LOG = KtorSimpleLogger("OrderService.kt")
class OrderService {
    companion object {

        suspend fun getOrder(id: String) : Order? {
            return OrderRepository.getOrder(id)
        }

        /**
         * Retrieves the order associated to a given cart. Returns null if not found
         */
        suspend fun getOrderByCartId(cartId: String) : Order? {
            return OrderRepository.getOrder(getUuidFromString(cartId))
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
                    ?.let { OrderRepository.updateOrder(it.copy(status = receiveOrder.status, created_at = receiveOrder.created_at)) }
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
        suspend fun createOrderFromCart(addressTo: AddressTo, cartId: String, user: User, testOrder: Boolean) : Order {
            val cart = CartService.getCart(cartId)
            val lineItems: ArrayList<LineItem> = ArrayList(emptyList())
            cart?.let {lineItems.addAll(cart.mugCartItemList.map {LineItem(it.mug.printifyId, it.amount, 69010)})}
            val newOrder = Order.create(
                getUuidFromString(cartId), // allows us to recognise an order by the cart it is linked to
                getOrderNextLabel(user.id, testOrder), // counts the number of orders of a given user
                lineItems,
                addressTo)

            // Upsert order in database
            OrderRepository.updateOrder(newOrder)

            // Adds order to the list
            OrderRepository.addOrderToUserOrderList(user.id, newOrder.external_id)

            return newOrder
        }

        /**
         * @return the next label when an order is created
         */
        private suspend fun getOrderNextLabel(userId : String, testOrder: Boolean) : String {
            val test = if (testOrder) "Test" else ""
            return "$test ${(OrderRepository.getUserOrderListByUserId(userId)?.orderIds?.size ?: 0) + 1}"
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
            return when (val printifyOrderPushResult = apiPlaceOrder(order.copy(status = Order.STATUS_PENDING))) {
                is PrintifyOrderPushFail -> {
                    printifyOrderPushResult
                }
                is PrintifyOrderPushSuccess -> {
                    // Get printifyId and store it in Order Object
                    OrderRepository.updateOrder(order.copy(id = printifyOrderPushResult.id, status = Order.STATUS_PENDING))
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
                apiCancelOrder(printifyId)
            }
        }

        /**
         * Refunds an order
         * @return the status returned by Stripe API
         */
        suspend fun refundOrder(localOrderId: String) : HttpStatusCode {
            // Refund the order
            // Get the correct apiKey based on if the order was a test order or a real order
            val order = getOrder(localOrderId)
            val isTestOrder = OrderService.getOrder(localOrderId)?.isTestOrder()
            LOG.debug("Refunding order of localId $localOrderId with value testOrder : $isTestOrder and label ${order?.label ?: "LABEL NOT FOUND"}")
            Stripe.apiKey = if (isTestOrder == false){
                System.getenv("STRIPE_API_KEY_REAL")
            } else {
                System.getenv("STRIPE_API_KEY_TEST")
            }

            val paymentIntentId = OrderService.getOrderStripePaymentIntent(localOrderId)
            if (paymentIntentId == ""){
                return HttpStatusCode(10, "Cannot refund if payment intent does not exist")
            }
            val params: MutableMap<String, Any> = HashMap()
            params["payment_intent"] = paymentIntentId

            val refund = Refund.create(params)
            return if (refund.status != "succeeded"){
                saveOrderPushResult(localOrderId, PrintifyOrderPushFail(refund.status, 10, "Refund failed", PrintifyOrderPushFailError(refund.failureReason, 10)), paymentIntentId)
                HttpStatusCode(10, "Refund did not succeed")
            } else {
                HttpStatusCode.OK
            }
        }


        /**
         * @param localOrderId the [Order.external_id] for which we want to retrieve the stored push result
         * @return a [String], the payment intent associated with an order
         */
        suspend fun getOrderStripePaymentIntent(localOrderId: String) : String {
            return OrderRepository.getOrderStripePaymentIntent(localOrderId)
        }


        /**
         * @param localOrderId the [Order.external_id] for which we want to retrieve the stored push result
         * @return a [PrintifyOrderPushResult] associated with the given order id, null if none has been saved
         */
        suspend fun getOrderPushResultByOrderId(localOrderId : String) : PrintifyOrderPushResult? {
            return OrderRepository.getOrderPushResultByOrderId(localOrderId)
        }

        /**
         * @param localOrderId the [Order.external_id] for which we want to store the push result
         * @param printifyOrderPushResult the [PrintifyOrderPushResult] to be stored
         */
        suspend fun saveOrderPushResult(localOrderId: String, printifyOrderPushResult: PrintifyOrderPushResult, paymentIntentId: String) {
            OrderRepository.saveOrderPushResult(localOrderId, printifyOrderPushResult, paymentIntentId)
        }

        suspend fun createUserOrderList(userId: String) : UserOrderList {
            val userOrderList = UserOrderList(userId, emptyList())
            OrderRepository.insertUserOrderList(userOrderList)
            return userOrderList
        }

        /**
         * Retrieves a user's list of orders
         * @param userId the id of the [User] for which to retrieve the list of past [Order]s
         * @return a [UserOrderList] if the list exists, null otherwise (shouldn't happen, but we never know)
         */
        suspend fun getUserOrderList(userId: String) : UserOrderList? {
            return OrderRepository.getUserOrderListByUserId(userId)
        }

        /**
         * Retrieves a list of [MugCartItem]s depending on an [Order.line_items] for display
         * @param orderId the local id of the [Order]
         * @return a [List] of [MugCartItem] corresponding to the order's [Order.line_items]
         */
        suspend fun getOrderLineItemsAsMugCartItems(orderId: String) : List<MugCartItem> {
            val order = OrderRepository.getOrder(orderId)
            return order?.line_items?.mapNotNull { line_item ->
                MugRepository.getMugByPrintifyId(line_item.product_id)?.let {
                    MugCartItem(
                        it,
                        line_item.quantity
                    )
                }
            } ?: emptyList()
        }

        /****************************************************************
         *
         *                      Webhook handling
         *
         ****************************************************************/

        fun constructEvent(payload: String, sigHeader: String, endpointSecret: String) : Event? {
            return try {
                Webhook.constructEvent(
                    payload, sigHeader, endpointSecret
                )
            } catch (e: SignatureVerificationException) {
                // Invalid signature
                println("Signature Verification Exception, payment signature invalid")
                null
            } catch (e: Exception) {
                // Invalid payload
                println("Invalid Payload")
                null
            }
        }

        /**
         * Handles a Stripe webhook [Event]
         * @return a [HttpStatusCode] to confirm there were no errors
         */
        suspend fun handleWebhookEvent(event: Event, testOrder: Boolean = false) : HttpStatusCode {
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
                    handleCheckoutSessionCompleted(stripeObject as Session, testOrder = testOrder)
                }

                else -> {
                    println("Unhandled event type: ${event.type}")
                    HttpStatusCode.OK
                }
            }
        }

        private suspend fun handleCheckoutSessionCompleted(stripeSession: Session, testOrder : Boolean) : HttpStatusCode {
            val sessionId : String? = stripeSession.clientReferenceId
            val session = SessionRepository.getSession(sessionId ?: "") ?: return HttpStatusCode.InternalServerError
            val user = session.user ?: return HttpStatusCode.InternalServerError
            val addressTo = stripeSession.customerDetails.toAddressTo()
            val paymentIntentId = stripeSession.paymentIntent

            LOG.debug("cartId to create order is ${session.cartId}. Test Order ?= $testOrder")
            // Create order
            val order = createOrderFromCart(addressTo, session.cartId, user, testOrder)
            // Updates session with a new cart, updated order id and updated user
            val newSession = SessionRepository.updateSession(
                session.copy(orderId = order.external_id,
                    cartId = CartRepository.createCart().id)
            )
            LOG.debug("After session update, new cartId  is ${newSession.cartId}")

            // Push order to printify and save result in database
            val pushResult = placeOrderToPrintify(order.external_id)
            saveOrderPushResult(order.external_id, pushResult, paymentIntentId)

            if (pushResult is PrintifyOrderPushSuccess){
                println("Order ${pushResult.id} pushed successfully")
            } else if (pushResult is PrintifyOrderPushFail) {
                println("${pushResult.message} because ${pushResult.errors.reason}")
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