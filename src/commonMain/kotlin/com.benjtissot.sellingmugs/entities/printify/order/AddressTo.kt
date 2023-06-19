package com.benjtissot.sellingmugs.entities.printify.order

import kotlinx.serialization.Serializable

@Serializable
data class AddressTo(
    val first_name: String,
    val last_name: String,
    val email: String,
    val phone: String,
    val country: String,
    val region: String,
    val address1: String,
    val address2: String,
    val city: String,
    val zip: String
) {
}