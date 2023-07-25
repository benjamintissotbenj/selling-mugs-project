package com.benjtissot.sellingmugs.entities.printify

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ProductLog(
    @SerialName("_id") val id: String,
    val printifyId: String,
    val message: String,
    val dateCreatedLocally: Instant,
    val dateProductCreated: Instant?,
    val dateProductPublished: Instant?
){}


