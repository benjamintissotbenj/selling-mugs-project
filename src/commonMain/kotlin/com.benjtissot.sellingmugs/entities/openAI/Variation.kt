package com.benjtissot.sellingmugs.entities.openAI

import kotlinx.serialization.Serializable

@Serializable
class ChatResponseContent(
    val variations: List<Variation>
){}

@Serializable
class Variation(
    val name: String,
    val parameters: String,
    val narrative: String,
) {
    fun getCleanName(): String{
        return this.name.substringAfter(":")
    }
}

