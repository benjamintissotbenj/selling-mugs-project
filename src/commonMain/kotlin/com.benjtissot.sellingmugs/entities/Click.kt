package com.benjtissot.sellingmugs.entities

import com.benjtissot.sellingmugs.CLICK_OBJECT_PATH
import com.benjtissot.sellingmugs.Const
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Click(
    @SerialName("_id") val id: String,
    val type : Const.ClickType,
    val time : Instant
    ){

    companion object {
        // Idea is that we don't need to define a route and requests in strings. Any changes only need to come from the models
        // then the client and the server are adjusted automatically
        const val path = CLICK_OBJECT_PATH
    }
}