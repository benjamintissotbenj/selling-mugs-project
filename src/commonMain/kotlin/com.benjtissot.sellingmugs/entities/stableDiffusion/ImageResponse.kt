package com.benjtissot.sellingmugs.entities.stableDiffusion

import kotlinx.serialization.Serializable

@Serializable
data class ImageResponse(
    val status: String,
    val id: Int? = null,
    val output: List<String>? = null,
    val eta: Float = 0f,
    val messege: String? = null // known error, comes from the Stable Diffusion API that has some issues
) {
    fun isRateLimitExceeded() : Boolean {
        return status == "error" && messege == "Rate limit exceeded"
    }
}