package com.benjtissot.sellingmugs.entities.openAI

import com.benjtissot.sellingmugs.Const
import kotlinx.serialization.Serializable

@Serializable
class ChatRequest(
    val model: String,
    val messages: ArrayList<Message>,
    val temperature: Float,
) {
    companion object {
        fun generateFromParams(parameters: ChatRequestParams) : ChatRequest {
            val promptStructure = when (parameters.type){
                Const.StableDiffusionImageType.REALISTIC -> {Const.getRealisticStructure()}
                Const.StableDiffusionImageType.GEOMETRIC -> {Const.getGeometricStructure()}
                Const.StableDiffusionImageType.CARTOON_ILLUSTRATION -> {Const.getCartoonStructure()}
            }
             val message = "$promptStructure \n ${Const.promptResponseStructure} ${parameters.subject}"

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
class ChatRequestParams(val subject : String, val type: Const.StableDiffusionImageType)