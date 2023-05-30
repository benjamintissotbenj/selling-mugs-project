package com.benjtissot.sellingmugs

class Const {
    enum class UserType {CLIENT, ADMIN}
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
}