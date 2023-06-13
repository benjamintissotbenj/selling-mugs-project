package com.benjtissot.sellingmugs.entities.printify

import kotlinx.serialization.Serializable

@Serializable
class ReceiveProduct(
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
    val external: External,
    val blueprint_id: Int = 535,
    val user_id: Int = 13158500,
    val shop_id: Int = 8965065,
    val print_provider_id: Int = 6,
    val print_areas: ArrayList<PrintArea>,
    val print_details: ArrayList<String>,
    val sales_channel_properties: ArrayList<String>,
    val twodaydelivery_enabled: Boolean
) {
}

@Serializable
class ProductOption(
    val name: String,
    val type: String,
    val values: List<ProductOptionValues>
){

}

@Serializable
class ProductOptionValues (
    val id: String,
    val title: String
    ){

}