package com.benjtissot.sellingmugs.entities

import com.benjtissot.sellingmugs.CART_OBJECT_PATH
import com.benjtissot.sellingmugs.MugCartItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Cart(
    @SerialName("_id") val id: String,
    val mugCartItemList: ArrayList<MugCartItem>,
){

    companion object {
        // Idea is that we don't need to define a route and requests in strings. Any changes only need to come from the models
        // then the client and the server are adjusted automatically
        const val path = CART_OBJECT_PATH
    }
}