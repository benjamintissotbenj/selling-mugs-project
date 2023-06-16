package com.benjtissot.sellingmugs.services

import com.benjtissot.sellingmugs.entities.printify.order.*
import com.benjtissot.sellingmugs.repositories.OrderRepository

class OrderService {
    companion object {

        suspend fun getOrder(id: String) : Order? {
            return OrderRepository.getOrder(id)
        }

        suspend fun getOrderFromPrintify(localId: String) : Order? {
            TODO("Implement")
        }

        suspend fun createOrderFromCart(addressTo: AddressTo) : Order {
            TODO("Implement")
        }

        suspend fun calculateOrderShippingCost(orderId: String) : ShippingCosts? {
            TODO("Implement")
        }

        suspend fun placeOrderToPrintify(orderId: String) : PrintifyOrderPushResult {
            TODO("Implement")
            // Get printifyId and store it in Order Object
        }

        suspend fun cancelOrder(orderId: String) {
            TODO("Implement")
        }

        suspend fun sendOrderToProduction(orderId: String) {
            TODO("Implement")
        }

    }
}