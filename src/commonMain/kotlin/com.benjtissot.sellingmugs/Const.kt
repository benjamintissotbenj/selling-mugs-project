package com.benjtissot.sellingmugs

class Const {
    enum class UserType {CLIENT, ADMIN}
    enum class ClickType(var type: String) {
        SEARCH("SEARCH"),
        HOME("HOME"),
        USER_INFO("USER_INFO"),
        CART("CART");

        override fun toString(): String {
            return type
        }
    }
}