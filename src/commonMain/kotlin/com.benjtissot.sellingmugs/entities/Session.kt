package com.benjtissot.sellingmugs.entities

import com.benjtissot.sellingmugs.SESSION_OBJECT_PATH
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Session(
    @SerialName("_id") val id: String,
    var user: User?,
    var lastUser: User?, // updated every time someone logs in but not wiped on logout
    var jwtToken: String,
    val clickDataId: String,
    val cartId: String
    ){

    companion object {
        // Idea is that we don't need to define a route and requests in strings. Any changes only need to come from the models
        // then the client and the server are adjusted automatically
        const val path = SESSION_OBJECT_PATH
    }
}