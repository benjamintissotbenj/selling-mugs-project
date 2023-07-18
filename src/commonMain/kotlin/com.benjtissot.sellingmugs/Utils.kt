package com.benjtissot.sellingmugs

import io.ktor.http.*

fun HttpStatusCode.print() : String {
    return "\tStatus ${this.value}, ${this.description};"
}