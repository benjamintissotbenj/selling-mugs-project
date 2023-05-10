package com.benjtissot.sellingmugs

import io.ktor.server.auth.*
import io.ktor.util.*
import org.komputing.khash.sha256.Sha256

class AuthUtil {
    companion object {
        val digestFunction = Sha256
    }
}