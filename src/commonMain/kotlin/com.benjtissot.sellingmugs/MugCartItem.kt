package com.benjtissot.sellingmugs

import com.benjtissot.sellingmugs.entities.Mug
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MugCartItem(
    @SerialName("_id") val id: String,
    val mug: Mug,
    val price: Float,
    var artID: String,
){

    companion object {
        // Idea is that we don't need to define a route and requests in strings. Any changes only need to come from the models
        // then the client and the server are adjusted automatically
        const val path = "/mugCartItem"
    }
}