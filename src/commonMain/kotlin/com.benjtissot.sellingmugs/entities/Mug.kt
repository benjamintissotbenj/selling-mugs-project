package com.benjtissot.sellingmugs.entities

import com.benjtissot.sellingmugs.MUG_OBJECT_PATH
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Mug(@SerialName("_id") val id: String,
               val printifyId: String,
               val name: String,
               var description: String,
               val price: Float,
               var artwork: Artwork){

    fun getBestPictureSrc() : String {
        return artwork.previewURLs.let {if (it.isEmpty()) artwork.imageURL else it[0]}
    }
    companion object {
        // Idea is that we don't need to define a route and requests in strings. Any changes only need to come from the models
        // then the client and the server are adjusted automatically
        const val path = MUG_OBJECT_PATH
    }
}