package com.benjtissot.sellingmugs.entities.printify.order

import kotlinx.serialization.Serializable

@Serializable
data class LineItem(
    val product_id: String,
    val quantity: Int,
    val variant_id: Int,
) {
}