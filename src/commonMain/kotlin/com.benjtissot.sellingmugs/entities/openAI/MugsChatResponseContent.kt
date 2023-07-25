package com.benjtissot.sellingmugs.entities.openAI

import com.benjtissot.sellingmugs.Const
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class MugsChatResponseContent(
    val variations: List<Variation>
){}

@Serializable
class Variation(
    val name: String,
    val parameters: String,
    val description: String,
    val narrative: String,
    @SerialName("negative_prompt") val negativePrompt: String,
) {
    fun getCleanName(): String{
        return this.name.substringAfter(":")
    }
}


@Serializable
class CategoriesChatResponseContent(val categories: List<CategoryResponse>){
}

@Serializable
class CategoryResponse(
    val category: String,
    val style: Const.StableDiffusionImageType
)