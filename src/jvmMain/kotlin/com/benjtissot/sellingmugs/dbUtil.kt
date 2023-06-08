package com.benjtissot.sellingmugs

import java.util.*

fun genUuid() : String {
    return UUID.randomUUID().toString()
}
fun getUuidFromString(input: String) : String {
    return UUID.nameUUIDFromBytes(input.encodeToByteArray()).toString()
}