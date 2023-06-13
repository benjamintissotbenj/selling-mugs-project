package com.benjtissot.sellingmugs.entities.printify

import kotlinx.serialization.Serializable

@Serializable
class PrintArea(
    val variant_ids : ArrayList<Int>,
    val placeholders : ArrayList<Placeholder>,
    val background: String = "#ffffff"
) {
}