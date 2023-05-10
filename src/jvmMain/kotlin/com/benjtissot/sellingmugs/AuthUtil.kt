package com.benjtissot.sellingmugs

import io.ktor.server.auth.*
import io.ktor.util.*

class AuthUtil {
    companion object {
        val digestFunction = getDigestFunction("SHA-256") { "ktor${it.length}" }
        val hashedUserTable = UserHashedTableAuth(
            table = mapOf(
                "jetbrains" to digestFunction("foobar"),
                "admin" to digestFunction("password")
            ),
            digester = digestFunction
        )
    }
}