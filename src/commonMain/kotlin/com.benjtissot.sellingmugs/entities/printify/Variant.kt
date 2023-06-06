package com.benjtissot.sellingmugs.entities.printify

import kotlinx.serialization.Serializable

@Serializable
class Variant(val id: Int = 69010,
              val price: Int = 600,
              val is_enabled: Boolean = true) {
}