package com.benjtissot.sellingmugs.entities.openAI

import io.ktor.http.*
import kotlinx.serialization.Serializable

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
