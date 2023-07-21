package com.benjtissot.sellingmugs.entities.openAI

import com.benjtissot.sellingmugs.entities.local.Category
import io.ktor.http.*
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GenerateCategoriesStatus (
    @SerialName("_id") val id: String,
    val statuses : List<GenerateCategoryStatus>,
    val dateGenerated : Instant,
)

@Serializable
class GenerateCategoryStatus (
    val category: Category,
    val message: String,
    val customStatusCodes : List<CustomStatusCode>
)

@Serializable
class CustomStatusCode (
    val value: Int,
    val description: String
) {
    fun toHttpStatusCode() : HttpStatusCode {
        return HttpStatusCode(this.value, this.description)
    }
}

fun HttpStatusCode.toCustom(): CustomStatusCode {
    return CustomStatusCode(this.value, this.description)
}
