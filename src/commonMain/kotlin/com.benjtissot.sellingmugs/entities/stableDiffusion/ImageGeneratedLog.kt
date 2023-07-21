package com.benjtissot.sellingmugs.entities.stableDiffusion

import com.benjtissot.sellingmugs.entities.openAI.Variation
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ImageGeneratedLog(
    @SerialName("_id") val id: String,
    val variationName: Variation,
    val imageURL: String,
    val message: String,
    val requestSubmitted: Instant,
    val responseReceived: Instant
){}


