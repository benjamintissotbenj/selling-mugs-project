package com.benjtissot.sellingmugs.entities.printify

import kotlinx.serialization.Serializable

@Serializable
class Variant(val id: Int,
              val price: Int,
              val is_enabled: Boolean) {
}