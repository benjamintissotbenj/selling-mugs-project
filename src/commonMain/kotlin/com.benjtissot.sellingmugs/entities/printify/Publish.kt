package com.benjtissot.sellingmugs.entities.printify

import kotlinx.serialization.Serializable

@Serializable
class Publish( val title: Boolean,
         val description: Boolean,
         val images: Boolean,
         val variants: Boolean,
         val tags: Boolean,
         val keyFeatures: Boolean,
         val shipping_template: Boolean) {
    companion object {
        fun default() : Publish {
            return Publish(
                title = true,
                description = true,
                images = true,
                variants = true,
                tags = true,
                keyFeatures = true,
                shipping_template = true
            )
        }
    }
}