package com.benjtissot.sellingmugs

class Const {
    enum class UserType {CLIENT, ADMIN}
    enum class ClickType(var type: String) {
        search("search"),
        home("home");

        override fun toString(): String {
            return type
        }
    }
}