package com.benjtissot.sellingmugs.entities.printify

import kotlinx.serialization.Serializable

@Serializable
class Publish( val title: Boolean = true,
         val description: Boolean = true,
         val images: Boolean = true,
         val variants: Boolean = true,
         val tags: Boolean = true,
         val keyFeatures: Boolean = true,
         val shipping_template: Boolean = true) {
}