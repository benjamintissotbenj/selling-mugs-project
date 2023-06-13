package com.benjtissot.sellingmugs.entities.printify

import kotlinx.serialization.Serializable

@Serializable
class Variant(val id: Int = 69010,
              val sku: String = "",
              val cost: Int =  599,
              val price: Int = 600,
              val title: String = "11oz",
              val grams: Int = 320,
              val is_enabled: Boolean = true,
              val is_default: Boolean = true,
              val is_available: Boolean =  true,
              val options: ArrayList<Int> = arrayListOf(1189),
              val quantity: Int = 1
) {
}