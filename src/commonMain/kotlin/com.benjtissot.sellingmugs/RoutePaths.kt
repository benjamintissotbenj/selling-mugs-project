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

const val HOMEPAGE_PATH = "/" // Only rendering path allowed to have a link in backend
const val USER_INFO_PATH = "/user_info"
const val CART_PATH = "/customer_cart"
const val HELLO_PATH = "/hello"
const val LOGIN_PATH = "/login"
const val REGISTER_PATH = "/register"
val ALL_FRONT_END_PATHS = listOf(HOMEPAGE_PATH, USER_INFO_PATH, CART_PATH, HELLO_PATH, LOGIN_PATH, REGISTER_PATH)

// Backend Route Paths

const val LOGOUT_PATH = "/logout"
const val CHECKOUT_PATH = "/checkout"
const val PAYMENT_PATH = "/payment"
const val USER_INFO_MESSAGE_PATH = "/user_info_message"
const val LOGIN_BACKEND_PATH = "/login_backend"
const val CHECK_REDIRECT_PATH = "/check_redirect"