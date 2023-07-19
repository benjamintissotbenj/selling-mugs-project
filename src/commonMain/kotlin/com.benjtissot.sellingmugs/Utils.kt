package com.benjtissot.sellingmugs

import com.benjtissot.sellingmugs.entities.openAI.CustomStatusCode
import io.ktor.http.*

fun HttpStatusCode.print() : String {
    return "\tStatus ${this.value}, ${this.description};"
}

fun CustomStatusCode.print() : String {
    return "\tStatus ${this.value}, ${this.description};"
}