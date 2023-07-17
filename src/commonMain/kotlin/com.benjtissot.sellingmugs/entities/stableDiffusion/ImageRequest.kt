package com.benjtissot.sellingmugs.entities.stableDiffusion

import kotlinx.serialization.Serializable

@Serializable
class ImageRequest(
    val key: String,
    val prompt: String,
    val negative_prompt: String,
    val width: String,
    val height: String,
    val samples: String,
    val num_inference_steps: String,
    val safety_checker: String,
    val enhance_prompt: String,
    val seed: String?,
    val guidance_scale: Float,
    val webhook: String?,
    val track_id: String?
) {
    companion object {
        fun generate(key: String, prompt: String) : ImageRequest {
            return ImageRequest(
                key = key,
                prompt = prompt,
                negative_prompt = "",
                width = "1024",
                height = "512",
                samples = "1",
                num_inference_steps = "20",
                safety_checker = "no",
                enhance_prompt = "yes",
                seed = null,
                guidance_scale = 7.5f,
                webhook = null,
                track_id = null
            )
        }
    }
}