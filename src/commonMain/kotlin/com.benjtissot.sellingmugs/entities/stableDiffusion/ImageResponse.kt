package com.benjtissot.sellingmugs.entities.stableDiffusion

import kotlinx.serialization.Serializable

@Serializable
class ImageResponse(
    val status: String,
    val id: Int,
    val output: List<String>,
    val eta: Float = 0f
) {
}