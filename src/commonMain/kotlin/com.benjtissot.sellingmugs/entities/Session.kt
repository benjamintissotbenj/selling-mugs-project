package com.benjtissot.sellingmugs.entities

import com.benjtissot.sellingmugs.Const
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Session(
    @SerialName("_id") val id: String,
    var user: User?,
    val clicks: List<Click>,
    //TODO: next specs we will be using in the NN
    ){

    companion object {
        // Idea is that we don't need to define a route and requests in strings. Any changes only need to come from the models
        // then the client and the server are adjusted automatically
        const val path = "/session"
    }
}