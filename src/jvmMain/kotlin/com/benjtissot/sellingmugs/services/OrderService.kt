package com.benjtissot.sellingmugs.services

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.entities.User
import com.benjtissot.sellingmugs.entities.printify.order.*
import com.benjtissot.sellingmugs.repositories.OrderRepository
import com.benjtissot.sellingmugs.repositories.OrderRepository.Companion.getOrderPrintifyId
import com.benjtissot.sellingmugs.repositories.SessionRepository
import com.benjtissot.sellingmugs.repositories.UserRepository
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
         * @param orderId the local id of the order to place to Printify
         * @return a [PrintifyOrderPushResult] object that holds all the information concerning the status of the
         * push order if it fails, and the printify ID if it is a success.
         */
        suspend fun placeOrderToPrintify(orderId: String) : PrintifyOrderPushResult {
            val order = getOrder(orderId) ?: return PrintifyOrderPushFail.notFoundInDatabase
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

    }
}