package com.benjtissot.sellingmugs.entities.openAI

import com.benjtissot.sellingmugs.GENERATE_CATEGORIES_STATUS_OBJECT_PATH
import com.benjtissot.sellingmugs.entities.local.Category
import io.ktor.http.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GenerateCategoriesStatus (
    @SerialName("_id") val id: String,
    val message: String,
    val requestParams: CategoriesChatRequestParams,
    val statuses : List<GenerateCategoryStatus>,
    val dateSubmitted : Instant,
    val dateReturned : Instant,
    val pending : Boolean = true
) {
    companion object {
        // Idea is that we don't need to define a route and requests in strings. Any changes only need to come from the models
        // then the client and the server are adjusted automatically
        const val path = GENERATE_CATEGORIES_STATUS_OBJECT_PATH
    }
    fun addStatus(catStat :GenerateCategoryStatus) : GenerateCategoriesStatus {
        val newStatuses = ArrayList(statuses)
        newStatuses.add(catStat)
        return this.copy(statuses = newStatuses, dateReturned = Clock.System.now())
    }

    fun finish() : GenerateCategoriesStatus {
        return this.copy(message = "Overall Success", dateReturned = Clock.System.now(), pending = false)
    }
}

@Serializable
class GenerateCategoryStatus (
    val category: Category,
    val message: String,
    val customStatusCodes : List<CustomStatusCode>,
    val dateSubmitted : Instant,
    val dateReturned : Instant,
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
