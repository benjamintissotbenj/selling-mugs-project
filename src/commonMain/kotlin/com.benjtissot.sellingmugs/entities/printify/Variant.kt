package com.benjtissot.sellingmugs.entities.printify

import com.benjtissot.sellingmugs.Const
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
                id = Const.variantId,
                sku = "",
                cost =  599,
                price = 720, // 6£ + 20% VAT
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