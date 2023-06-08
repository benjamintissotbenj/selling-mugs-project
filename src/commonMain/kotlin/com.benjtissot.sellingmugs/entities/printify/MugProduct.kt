package com.benjtissot.sellingmugs.entities.printify

import kotlinx.serialization.Serializable

@Serializable
class MugProduct(
    val id: String = "",
    val title: String,
    val description: String,
    val blueprint_id: Int = 535,
    val print_provider_id: Int = 6,
    val variants: ArrayList<Variant>,
    val print_areas: ArrayList<PrintArea>
    ) {
}

@Serializable
class MugProductInfo(
    val title: String,
    val description: String,
    val image: Image
){
    /**
     * Creates a MugProduct from a MugProductInfo
     */
    fun toMugProduct() : MugProduct {
        val placeholder = Placeholder("front", arrayListOf(image))
        val variants = arrayListOf(Variant())
        val print_areas = arrayListOf(
            PrintArea(
                variant_ids = variants.map { it.id } as ArrayList<Int>,
                placeholders = arrayListOf(placeholder)
            )
        )

        val mugProduct = MugProduct(
            title = title,
            description = description,
            variants = variants,
            print_areas = print_areas
        )

        return mugProduct
    }
}