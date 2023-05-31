package com.benjtissot.sellingmugs.entities.printify

import kotlinx.serialization.Serializable

@Serializable
class MugProduct(
    val title: String,
    val description: String,
    val blueprint_id: Int = 535,
    val print_provider_id: Int = 6,
    val variants: ArrayList<Variant>,
    val print_areas: ArrayList<PrintArea>
    ) {
}