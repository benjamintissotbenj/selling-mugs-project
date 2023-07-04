package com.benjtissot.sellingmugs.entities.printify

import kotlinx.serialization.Serializable

@Serializable
data class ReceiveProduct(
    val id: String = "",
    val title: String,
    val description: String,
    val tags: ArrayList<String>,
    val options: ArrayList<ProductOption>,
    val variants: ArrayList<Variant>,
    val images: ArrayList<ImageForProduct>,
    val created_at: String,
    val updated_at: String,
    val visible: Boolean,
    val is_locked: Boolean,
    val external: External? = null,
    val blueprint_id: Int,
    val user_id: Int,
    val shop_id: Int,
    val print_provider_id: Int,
    var print_areas: ArrayList<PrintArea>,
    val print_details: ArrayList<String>,
    val sales_channel_properties: ArrayList<String>,
    val twodaydelivery_enabled: Boolean
) {
    fun changeImage(newImage: Image) : UpdateProductImage {
        val placeholder = Placeholder("front", arrayListOf(newImage))
        val variants = arrayListOf(Variant.default())
        val print_areas = arrayListOf(
            PrintArea(
                variant_ids = variants.map { it.id } as ArrayList<Int>,
                placeholders = arrayListOf(placeholder),
                background = "#ffffff"
            )
        )
        return UpdateProductImage(print_areas = print_areas)
    }
}

@Serializable
data class UpdateProductImage(
    var print_areas: ArrayList<PrintArea>
){}

@Serializable
class ProductOption(
    val name: String,
    val type: String,
    val values: List<ProductOptionValues>
){

}

@Serializable
class ProductOptionValues (
    val id: Int,
    val title: String
    ){

}