package com.benjtissot.sellingmugs.entities.local

import com.benjtissot.sellingmugs.CATEGORY_OBJECT_PATH
import com.benjtissot.sellingmugs.Const
import com.benjtissot.sellingmugs.MUG_OBJECT_PATH
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Mug(@SerialName("_id") val id: String,
               val printifyId: String,
               val name: String,
               var description: String,
               val price: Float,
               val category: Category = Category(),
               var artwork: Artwork
){

    fun getBestPictureSrc() : String {
        return artwork.previewURLs.let {if (it.isEmpty()) artwork.imageURL else it[0]}
    }

    fun getAllPictureSrcs() : List<String> {
        val srcArrayList = ArrayList(artwork.previewURLs)
        srcArrayList.add(artwork.imageURL)
        return srcArrayList.toList()
    }
    companion object {
        // Idea is that we don't need to define a route and requests in strings. Any changes only need to come from the models
        // then the client and the server are adjusted automatically
        const val path = MUG_OBJECT_PATH
    }
}



@Serializable
data class UserCustomMugList (
    @SerialName("_id") val userId: String,
    val mugIds: List<String>,
){

}

@Serializable
data class MugFilter(
    val currentPage: Int? = null,
    val publicOnly: Boolean = true,
    val categories: List<Category> = emptyList()
)

@Serializable
data class Category (
    @SerialName("_id") val id : String = "0",
    val name: String = Const.mugCategoryDefault
){
    companion object {
        val path = CATEGORY_OBJECT_PATH
    }
}