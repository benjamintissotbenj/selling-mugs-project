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
    fun addStatus(catStat : GenerateCategoryStatus) : GenerateCategoriesStatus {
        val newStatuses = ArrayList(statuses)
        newStatuses.add(catStat)
        return this.copy(statuses = newStatuses, dateReturned = Clock.System.now())
    }

    /**
     * Updates a category status in this object. If it isn't in the list of statuses, adds it
     */
    fun updateStatus(catStat : GenerateCategoryStatus) : GenerateCategoriesStatus {
        // If the status is already in the status list, replace it
        return if (statuses.map { it.category.id }.contains(catStat.category.id)){
            val newStatuses = ArrayList(statuses)
            newStatuses[newStatuses.indexOf(newStatuses.find { it.category.id == catStat.category.id })] = catStat
            this.copy(statuses = newStatuses, dateReturned = Clock.System.now())
        } // If it isn't, add it
        else {
            addStatus(catStat)
        }
    }

    fun finish() : GenerateCategoriesStatus {
        val successPercentage = calculateSuccessPercentage()
        val message = if (successPercentage >= 80) {"Overall Success"} else if (successPercentage >= 30) {"Partial success"} else {"Unsufficient success"}
        return this.copy(message = message, dateReturned = Clock.System.now(), pending = false)
    }
}

@Serializable
data class GenerateCategoryStatus (
    val category: Category,
    val message: String,
    val customStatusCodes : List<CustomStatusCode>,
    val dateSubmitted : Instant,
    val dateReturned : Instant,
) {
    fun addCustomStatusCode(customStatusCode : CustomStatusCode) : GenerateCategoryStatus {
        val newStatuses = ArrayList(customStatusCodes)
        newStatuses.add(customStatusCode)
        return this.copy(customStatusCodes = newStatuses, dateReturned = Clock.System.now())
    }
}

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



fun calculateSuccessPercentage(statusCodes : List<CustomStatusCode>, expectedSize : Int) : Int {
    if (statusCodes.isEmpty()) return 0
    return (statusCodes.filter { it.value == 200 }.size.toFloat() / expectedSize * 100f).toInt()
}

fun GenerateCategoriesStatus.calculateSuccessPercentage() : Int {
    return (this.statuses.sumOf { calculateSuccessPercentage(it.customStatusCodes, this.requestParams.amountOfVariations) }.toFloat() / this.statuses.size.toFloat()).toInt()
}

fun GenerateCategoriesStatus.calculateCompletionPercentage() : Int {
    return (this.statuses.sumOf { stat -> stat.calculateCompletionPercentage(this.requestParams.amountOfVariations) }.toFloat() / this.requestParams.amountOfCategories.toFloat()).toInt()
}

fun GenerateCategoryStatus.calculateCompletionPercentage(expectedSize : Int) : Int {
    return ((this.customStatusCodes.size.toFloat() / expectedSize.toFloat())*100f).toInt()
}