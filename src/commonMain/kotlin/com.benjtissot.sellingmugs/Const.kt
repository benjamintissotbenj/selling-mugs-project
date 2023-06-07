package com.benjtissot.sellingmugs

class Const {
    enum class UserType(var type: String) {
        CLIENT("CLIENT"),
        ADMIN("ADMIN"),;

        override fun toString(): String {
            return type
        }
    }
    enum class ClickType(var type: String) {
        SEARCH_NAV("SEARCH_NAV"),
        HOME_NAV("HOME_NAV"),
        USER_INFO_NAV("USER_INFO_NAV"),
        CHECKOUT_NAV("CHECKOUT_NAV"),
        CART_NAV("CART_NAV"),
        CHECKOUT_CART("CHECKOUT_CART"),;

        override fun toString(): String {
            return type
        }
    }

    enum class ColorCode(var colourCode: String) {
        BLUE("#007bff"),
        RED("#ff0b00"),
        ;
        fun code(): String {
            return colourCode
        }
    }
}