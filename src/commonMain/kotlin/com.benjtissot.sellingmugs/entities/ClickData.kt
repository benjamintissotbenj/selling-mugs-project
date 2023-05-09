package com.benjtissot.sellingmugs.entities

import com.benjtissot.sellingmugs.Const
import com.benjtissot.sellingmugs.SESSION_PATH
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClickData(
    @SerialName("_id") val id: String,
    val clicks: ArrayList<Click>,
    ){

    companion object {
        // Idea is that we don't need to define a route and requests in strings. Any changes only need to come from the models
        // then the client and the server are adjusted automatically
        const val path = SESSION_PATH
    }
}