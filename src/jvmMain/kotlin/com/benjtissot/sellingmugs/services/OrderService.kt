package com.benjtissot.sellingmugs.services

import com.benjtissot.sellingmugs.apiGetOrder
import com.benjtissot.sellingmugs.apiPlaceOrder
import com.benjtissot.sellingmugs.entities.printify.order.*
import com.benjtissot.sellingmugs.genUuid
import com.benjtissot.sellingmugs.repositories.OrderRepository
import com.benjtissot.sellingmugs.repositories.OrderRepository.Companion.getOrderPrintifyId
import io.ktor.client.call.*
import io.ktor.http.*

class OrderService {
    companion object {

        suspend fun getOrder(id: String) : Order? {
            return OrderRepository.getOrder(id)
        }
        suspend fun getOrderByPrintifyId(printifyId: String) : Order? {
            return OrderRepository.getOrderByPrintifyId(printifyId)
        }

        suspend fun getOrderFromPrintify(localId: String) : Order? {
            val httpResponse = apiGetOrder(getOrderPrintifyId(localId))
            return if (httpResponse.status == HttpStatusCode.OK){
                val receiveOrder = httpResponse.body<ReceiveOrder>()
                getOrderByPrintifyId(receiveOrder.id) // Printify id
            } else {
                null
            }
        }

        /**
         * Creates an order and populates it with the correct information of what is in the cart
         * @param addressTo the address to which deliver the order
         * @param cartId the id of the cart to populate the order with
         * @return the [Order] created and added to the database
         */
        suspend fun createOrderFromCart(addressTo: AddressTo, cartId: String) : Order {
            val cart = CartService.getCart(cartId)
            val lineItems: ArrayList<LineItem> = ArrayList(emptyList())
            cart?.let {lineItems.addAll(cart.mugCartItemList.map {LineItem(it.mug.printifyId, it.amount, 69010)})}
            val newOrder = Order.create(genUuid(), OrderRepository.getOrderNextLabel(), lineItems, addressTo)
            // Insert order in database
            OrderRepository.insertOrder(newOrder)
            return newOrder
        }

        suspend fun calculateOrderShippingCost(orderId: String) : ShippingCosts? {
            TODO("Implement")
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

        suspend fun cancelOrder(orderId: String) {
            TODO("Implement")
        }

        suspend fun sendOrderToProduction(orderId: String) {
            TODO("Implement")
        }

    }
}