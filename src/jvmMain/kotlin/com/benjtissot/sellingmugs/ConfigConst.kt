package com.benjtissot.sellingmugs

class ConfigConst {
    companion object {
        val PORT = System.getenv(Const.PORT_STRING)?.toInt() ?: 9090
        val HOST = System.getenv(Const.HOST_STRING) ?: "localhost"
        const val SECRET = "secret"
        val ISSUER = "$HOST:$PORT/"
        val AUDIENCE = "$HOST:$PORT/"
        const val REALM = "Access to 'hello'"
    }
}