package com.benjtissot.sellingmugs.services

import com.benjtissot.sellingmugs.MugCartItem
import com.benjtissot.sellingmugs.entities.Cart
import com.benjtissot.sellingmugs.entities.Mug
import com.benjtissot.sellingmugs.entities.printify.order.AddressTo
import com.benjtissot.sellingmugs.entities.printify.order.Order
import com.benjtissot.sellingmugs.entities.printify.order.ShippingCosts
import com.benjtissot.sellingmugs.genUuid
import com.benjtissot.sellingmugs.repositories.CartRepository
import com.benjtissot.sellingmugs.repositories.OrderRepository

class OrderService {
    companion object {

        suspend fun getOrder(id: String) : Order? {
            return OrderRepository.getOrder(id)
        }

        suspend fun createOrderFromCart(addressTo: AddressTo) : Order {
            TODO("Implement")
        }

        suspend fun calculateOrderShippingCost(orderId: String) : ShippingCosts? {
            TODO("Implement")
        }

        suspend fun placeOrderToPrintify(orderId: String) {
            TODO("Implement")
        }

        suspend fun cancelOrder(orderId: String) {
            TODO("Implement")
        }

        suspend fun sendOrderToProduction(orderId: String) {
            TODO("Implement")
        }

    }
}