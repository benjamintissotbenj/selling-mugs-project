package com.benjtissot.sellingmugs.entities

import com.benjtissot.sellingmugs.entities.printify.order.AddressTo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddressInfo(
    @SerialName("_id") val id: String,
    val addressTo: AddressTo
) {
}