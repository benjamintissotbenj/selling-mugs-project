package com.benjtissot.sellingmugs.entities.printify.order

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Order(
    @SerialName("_id") val external_id: String, // parameter name for json serialisation
    val label: String,
    val line_items: List<LineItem>,
    val address_to: AddressTo,
    val send_shipping_notification: Boolean = true,
    val shipping_method: Int = 1,
) {
}
@Serializable
data class OrderToCalculateShippingCosts(
    val line_items: List<LineItem>,
    val address_to: AddressTo,
) {
}