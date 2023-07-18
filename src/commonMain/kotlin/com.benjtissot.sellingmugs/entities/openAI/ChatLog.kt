package com.benjtissot.sellingmugs.entities.openAI

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ChatLog(
    @SerialName("_id") val id: String,
    val chatRequest: ChatRequest,
    val chatResponse: ChatResponseContent?,
    val message: String,
    val responseReceived: Instant
){}


