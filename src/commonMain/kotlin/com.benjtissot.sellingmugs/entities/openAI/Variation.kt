package com.benjtissot.sellingmugs.entities.openAI

import kotlinx.serialization.Serializable

@Serializable
class ChatResponseContent(
    val variations: List<Variation>
){}

@Serializable
class Variation(
    val parameters: String,
    val narrative: String,
) {
}

