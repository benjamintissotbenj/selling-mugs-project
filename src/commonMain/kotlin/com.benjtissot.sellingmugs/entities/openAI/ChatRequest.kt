package com.benjtissot.sellingmugs.entities.openAI

import com.benjtissot.sellingmugs.Const
import com.benjtissot.sellingmugs.Const.Companion.getGenerateCategoriesPrompt
import kotlinx.serialization.Serializable

@Serializable
class ChatRequest(
    val model: String,
    val messages: ArrayList<Message>,
    val temperature: Float,
) {
    companion object {
        fun generateMugRequestFromParams(parameters: MugsChatRequestParams) : ChatRequest {
            val promptStructure = when (parameters.type){
                Const.StableDiffusionImageType.REALISTIC -> {Const.getRealisticStructure()}
                Const.StableDiffusionImageType.GEOMETRIC -> {Const.getGeometricStructure()}
                Const.StableDiffusionImageType.CARTOON_ILLUSTRATION -> {Const.getCartoonStructure()}
            }
             val message = "$promptStructure \n ${Const.getPromptResponseStructure(parameters.amountOfVariations)} ${parameters.subject}"

            return ChatRequest("gpt-3.5-turbo", arrayListOf(Message("user", content = message)), temperature = 0.7f)
        }

        fun generateCategoryRequestFromParams(amountOfCategories: Int) : ChatRequest {
             val message = getGenerateCategoriesPrompt(amountOfCategories)
            return ChatRequest("gpt-3.5-turbo", arrayListOf(Message("user", content = message)), temperature = 0.7f)
        }
    }
}

@Serializable
class Message(
    val role: String,
    val content: String,
){
}

@Serializable
class MugsChatRequestParams(val subject : String, val type: Const.StableDiffusionImageType, val amountOfVariations: Int)

@Serializable
class CategoriesChatRequestParams(val amountOfCategories: Int, val amountOfVariations: Int, val type: Const.StableDiffusionImageType?)