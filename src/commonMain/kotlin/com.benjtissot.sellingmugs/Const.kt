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
        HOME_NAV("HOME_NAV"),
        PROFILE_NAV("PROFILE_NAV"),
        CART_NAV("CART_NAV"),
        CHECKOUT_CART("CHECKOUT_CART"),
        LOGIN("LOGIN"),
        REGISTER("REGISTER"),
        LOGOUT("LOGOUT"),
        USER_INFO_TAB("USER_INFO_TAB"),
        USER_INFO_ORDER_TAB("USER_INFO_ORDER_TAB"),
        ADD_MUG_TO_CART("ADD_MUG_TO_CART"),
        CUSTOMISED_MUG("CUSTOMISED_MUG"),
        TEST_PAY("TEST_PAY"),
        REAL_PAY("REAL_PAY"),
        ;

        override fun toString(): String {
            return type
        }
    }

    enum class ColorCode(var colourCode: String) {
        BLUE("#1976d2"),
        LIGHT_BLUE("#e0ecf4"),
        DARK_BLUE("#0088d0"),
        BACKGROUND_GREY("#f7f7f7"),
        BACKGROUND_GREY_DARK("#f0f0f0"),
        BACKGROUND_GREY_DARKER("#e0e0e0"),
        BACKGROUND_GREY_EVEN_DARKER("#d0d0d0"),
        BACKGROUND_GREY_DARKEST("#c0c0c0"),
        RED("#ff0b00"),
        ;
        fun code(): String {
            return colourCode
        }
    }

    companion object {
        val ORDER_FILTER_ALL = "All"
        val ORDER_FILTER_SIX_MONTHS = "< 6 months"
        val ORDER_FILTER_THREE_MONTHS = "< 3 months"
    }
}