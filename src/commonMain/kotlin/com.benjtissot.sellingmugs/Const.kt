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
        CART_CHECKOUT("CART_CHECKOUT"),
        LOGIN("LOGIN"),
        REGISTER("REGISTER"),
        LOGOUT("LOGOUT"),
        USER_INFO_TAB("USER_INFO_TAB"),
        USER_INFO_ORDER_TAB("USER_INFO_ORDER_TAB"),
        SHOW_ORDER_DETAILS("SHOW_ORDER_DETAILS"),
        CANCEL_ORDER("CANCEL_ORDER"),
        ADD_MUG_TO_CART("ADD_MUG_TO_CART"),
        TEST_PAY("TEST_PAY"),
        REAL_PAY_POPUP("REAL_PAY_POPUP"),
        CLOSE_WAITING_FOR_PAYMENT("CLOSE_WAITING_FOR_PAYMENT"),
        CONFIRM_REAL_PAY("CONFIRM_REAL_PAY"),
        CUSTOM_MUG_OPEN_PAGE("CUSTOM_MUG_OPEN_PAGE"),
        CUSTOM_MUG_UPLOAD_IMAGE("CUSTOM_MUG_UPLOAD_IMAGE"),
        CUSTOM_MUG_REFRESH_PREVIEW("CUSTOM_MUG_REFRESH_PREVIEW"),
        CUSTOM_MUG_ADD_TO_CART("CUSTOM_MUG_ADD_TO_CART"),
        CART_SEE_AVAILABLE_MUGS("CART_SEE_AVAILABLE_MUGS"),
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
        // Environment Variable names
        const val PORT_STRING = "PORT"
        const val HOST_STRING = "HOST"
        const val STRIPE_WEBHOOK_SECRET_TEST_STRING = "STRIPE_WEBHOOK_SECRET_TEST"
        const val STRIPE_WEBHOOK_SECRET_REAL_STRING = "STRIPE_WEBHOOK_SECRET_REAL"
        const val STRIPE_API_KEY_TEST_STRING = "STRIPE_API_KEY_TEST"
        const val STRIPE_API_KEY_REAL_STRING = "STRIPE_API_KEY_REAL"
        const val PRINTIFY_STORE_ID_STRING = "PRINTIFY_STORE_ID"
        const val MONGODB_URI_STRING = "MONGODB_URI"
        const val MONGODB_DBNAME_STRING = "MONGODB_DBNAME"
        const val IS_PRODUCTION_STRING = "ORG_GRADLE_PROJECT_isProduction"

        // Filter Order
        const val ORDER_FILTER_ALL = "All"
        const val ORDER_FILTER_SIX_MONTHS = "< 6 months"
        const val ORDER_FILTER_THREE_MONTHS = "< 3 months"

        // Query params
        const val updateType = "updateType"
        const val titleDesc = "titleDesc"
        const val image = "image"
        const val cartId = "cartId"
        const val orderId = "orderId"

        // Call params
        const val id = "id"
        const val path = "path"
        const val productId = "productId"
        const val clickDataId = "clickDataId"
        const val clickType = "clickType"
        const val localOrderId = "localOrderId"
        const val userId = "userId"
        const val public = "public"
        const val printifyId = "printifyId"
        const val mugId = "mugId"

        // Stripe params
        const val payment_intent = "payment_intent"

    }
}