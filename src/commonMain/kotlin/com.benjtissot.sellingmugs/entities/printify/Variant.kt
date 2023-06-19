package com.benjtissot.sellingmugs.entities.printify

import kotlinx.serialization.Serializable

@Serializable
class Variant(val id: Int,
              val sku: String,
              val cost: Int,
              val price: Int,
              val title: String,
              val grams: Int,
              val is_enabled: Boolean,
              val is_default: Boolean,
              val is_available: Boolean,
              val options: ArrayList<Int>,
              val quantity: Int
) {
    companion object {
        fun default() : Variant {
            return Variant(
                id = 69010,
                sku = "",
                cost =  599,
                price = 600,
                title = "11oz",
                grams = 320,
                is_enabled = true,
                is_default = true,
                is_available =  true,
                options = arrayListOf(1189),
                quantity = 1
            )
        }
    }
}