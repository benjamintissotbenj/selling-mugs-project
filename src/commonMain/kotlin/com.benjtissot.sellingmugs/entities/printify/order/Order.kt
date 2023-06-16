package com.benjtissot.sellingmugs.entities.printify.order

import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Order(
    @SerialName("_id") val external_id: String, // parameter name for json serialisation
    val label: String,
    val line_items: List<LineItem>,
    val address_to: AddressTo,
    val shipping_method: Int = 1, // 1 is standard, 2 is express
    val send_shipping_notification: Boolean = true,
    val id: String = "", // Printify id
    val status: String = "on-hold",
) {
    fun getCalculateShipping(): OrderToCalculateShippingCosts {
        return OrderToCalculateShippingCosts(line_items, address_to)
    }
}

@Serializable
data class OrderToCalculateShippingCosts(
    val line_items: List<LineItem>,
    val address_to: AddressTo,
) {
}

@Serializable
data class ShippingCosts(
    val standard: Int,
    val express: Int,
) {
}

interface PrintifyOrderPushResult {

}

@Serializable
data class PrintifyOrderPushSuccess (
    val id: String,
) : PrintifyOrderPushResult {
}

@Serializable
data class PrintifyOrderPushFail (
    val status: String,
    val code: Int,
    val message: String,
    val errors: PrintifyOrderPushFailError
) : PrintifyOrderPushResult {
}
@Serializable
data class PrintifyOrderPushFailError (
    val reason: String,
    val code: Int
) {
}