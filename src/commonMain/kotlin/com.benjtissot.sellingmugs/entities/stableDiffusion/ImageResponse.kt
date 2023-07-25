package com.benjtissot.sellingmugs.entities.stableDiffusion

import kotlinx.serialization.Serializable

@Serializable
class ImageResponse(
    val status: String,
    val id: Int? = null,
    val output: List<String>? = null,
    val eta: Float = 0f
) {
}