package com.benjtissot.sellingmugs.entities.local

import com.benjtissot.sellingmugs.entities.local.Mug
import kotlinx.serialization.Serializable

@Serializable
data class MugCartItem(
    val mug: Mug,
    var amount: Int,
){

    companion object {
        // Idea is that we don't need to define a route and requests in strings. Any changes only need to come from the models
        // then the client and the server are adjusted automatically
        const val path = "/mugCartItem"
    }
}