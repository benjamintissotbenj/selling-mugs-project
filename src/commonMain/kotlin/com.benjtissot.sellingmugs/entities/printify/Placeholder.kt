package com.benjtissot.sellingmugs.entities.printify

import kotlinx.serialization.Serializable

@Serializable
class Placeholder(val position : String,
                  val images : ArrayList<Image>) {
}