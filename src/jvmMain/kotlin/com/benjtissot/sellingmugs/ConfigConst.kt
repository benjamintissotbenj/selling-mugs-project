package com.benjtissot.sellingmugs

class ConfigConst {
    companion object {
        const val SECRET = "secret"
        val ISSUER = "${System.getenv(Const.HOST)}:${System.getenv(Const.PORT).toInt()}/"
        val AUDIENCE = "${System.getenv(Const.HOST)}:${System.getenv(Const.PORT).toInt()}/"
        const val REALM = "Access to 'hello'"
    }
}