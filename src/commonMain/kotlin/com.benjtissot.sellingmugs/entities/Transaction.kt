package com.benjtissot.sellingmugs.entities

import com.benjtissot.sellingmugs.TRANSACTION_OBJECT_PATH
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Transaction(
    @SerialName("_id") val id: String,
    val userID: String,
    val cart: Cart,
    ){

    companion object {
        // Idea is that we don't need to define a route and requests in strings. Any changes only need to come from the models
        // then the client and the server are adjusted automatically
        const val path = TRANSACTION_OBJECT_PATH
    }
}