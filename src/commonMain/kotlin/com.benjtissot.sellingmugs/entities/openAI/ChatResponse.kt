package com.benjtissot.sellingmugs.entities.openAI

import kotlinx.serialization.Serializable

@Serializable
class ChatResponse(
    val choices: ArrayList<Choice>,
) {
}

@Serializable
class Choice(
    val index: Int,
    val message: Message,
    val finish_reason: String,
){
}