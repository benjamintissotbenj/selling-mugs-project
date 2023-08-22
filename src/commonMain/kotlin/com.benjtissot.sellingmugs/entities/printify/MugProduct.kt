package com.benjtissot.sellingmugs.entities.printify

import com.benjtissot.sellingmugs.Const
import kotlinx.serialization.Serializable

@Serializable
class MugProduct(
    val id: String,
    val title: String,
    val description: String,
    val blueprint_id: Int,
    val print_provider_id: Int,
    val variants: ArrayList<Variant>,
    val print_areas: ArrayList<PrintArea>
    ) {
}

@Serializable
class MugProductInfo(
    val title: String,
    val description: String,
    val categoryName: String,
    val image: Image,
    val fullPrompt: String? = null
){
    /**
     * Creates a MugProduct from a MugProductInfo
     */
    fun toMugProduct(): MugProduct {
        val placeholder = Placeholder("front", arrayListOf(image))
        val variants = arrayListOf(Variant.default())
        val print_areas = arrayListOf(
            PrintArea(
                variant_ids = variants.map { it.id } as ArrayList<Int>,
                placeholders = arrayListOf(placeholder),
                background = "#ffffff"
            )
        )

        return MugProduct(
            id = "",
            title = title,
            description = description,
            variants = variants,
            print_areas = print_areas,
            blueprint_id = Const.blueprintId,
            print_provider_id = Const.printProviderId
        )
    }
}