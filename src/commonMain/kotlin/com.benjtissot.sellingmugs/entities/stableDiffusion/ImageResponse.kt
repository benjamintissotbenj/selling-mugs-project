package com.benjtissot.sellingmugs.entities.stableDiffusion

import kotlinx.serialization.Serializable

@Serializable
class ImageResponse(
    val output: List<String>
) {
}