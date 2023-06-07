package com.benjtissot.sellingmugs.entities

import com.benjtissot.sellingmugs.ARTWORK_OBJECT_PATH
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Artwork(
    @SerialName("_id") val id: String,
    val imageURL: String,
    val public: Boolean = true // tells if it is artwork that should be available to anyone
    ){

    companion object {
        // Idea is that we don't need to define a route and requests in strings. Any changes only need to come from the models
        // then the client and the server are adjusted automatically
        const val path = ARTWORK_OBJECT_PATH
    }
}