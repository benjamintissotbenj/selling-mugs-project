package com.benjtissot.sellingmugs

// Object paths
const val SESSION_OBJECT_PATH = "/session"
const val CLICK_DATA_OBJECT_PATH = "/click_data"
const val ARTWORK_OBJECT_PATH = "/artwork"
const val USER_OBJECT_PATH = "/user"
const val QUESTIONNAIRE_OBJECT_PATH = "/questionnaire"
const val MUG_OBJECT_PATH = "/mug"
const val TRANSACTION_OBJECT_PATH = "/transaction"
const val CLICK_OBJECT_PATH = "/click"
const val CART_OBJECT_PATH = "/cart"

// Rendering Paths

const val HOMEPAGE_PATH = "/" // Only rendering path allowed to have a "get" link in backend
const val USER_INFO_PATH = "/user_info"
const val ADMIN_PANEL_PATH = "/admin_panel"
const val CART_PATH = "/customer_cart"
const val CHECKOUT_PATH = "/checkout"
const val LOGIN_PATH = "/login"
const val REGISTER_PATH = "/register"
val ALL_FRONT_END_PATHS = listOf(HOMEPAGE_PATH, USER_INFO_PATH, CART_PATH, CHECKOUT_PATH, LOGIN_PATH, REGISTER_PATH, ADMIN_PANEL_PATH)

// Backend Route Paths

const val LOGOUT_PATH = "/logout"
const val CHECKOUT_BACKEND_PATH = "/checkout_backend"
const val PAYMENT_PATH = "/payment"
const val USER_INFO_MESSAGE_PATH = "/user_info_message"
const val LOGIN_BACKEND_PATH = "/login_backend"
const val CHECK_REDIRECT_PATH = "/check_redirect"
const val PRINTIFY_PATH = "/printify"
const val CREATE_PRODUCT_PATH = "/create"
const val PUBLISH_PRODUCT_PATH = "/publish"
const val UPLOAD_IMAGE_PATH = "/upload"