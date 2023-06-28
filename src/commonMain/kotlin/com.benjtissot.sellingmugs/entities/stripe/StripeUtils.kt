package com.benjtissot.sellingmugs.entities.stripe

import kotlin.math.roundToInt

val paramSessionId = "client_reference_id"
val prefilledEmail = "prefilled_email"

/**
 * Gets the correct payment link for real orders depending on the amount of mugs
 * @param sessionId the id of the current session, to be linked when the payment goes through
 * @param email the user email to be pre-filled in payment page
 */
fun getPaymentLink(amountOfMugs: Int, sessionId: String, email: String = "") : String {
    val baseLink = when (amountOfMugs) {
        1 -> "https://buy.stripe.com/dR66pHgsJ2aB5HObIL"
        2 -> "https://buy.stripe.com/3cs7tLgsJ9D35HOdQR"
        3 -> "https://buy.stripe.com/8wMdS9gsJ16x1rydQU"
        4 -> "https://buy.stripe.com/7sI6pH7WdbLb8U028d"
        5 -> "https://buy.stripe.com/28o4hz90hcPf4DK006"
        6 -> "https://buy.stripe.com/9AQ7tL5O516x1ry5kr"
        7 -> "https://buy.stripe.com/eVa3dv1xPeXn7PW6ow"
        8 -> "https://buy.stripe.com/00geWddgx16xfioaEN"
        9 -> "https://buy.stripe.com/28o15n5O59D32vCfZ8"
        10 -> "https://buy.stripe.com/eVa9BTb8peXneekcMX"
        else -> return ""
    }
    return "$baseLink?locale=en&$paramSessionId=$sessionId" + if (email.isNotBlank()) "&$prefilledEmail=$email" else email
}

/**
 * Gets the correct test payment link depending on the amount of mugs
 * @param sessionId the id of the current session, to be linked when the payment goes through
 * @param email the user email to be pre-filled in payment page
 */
fun getPaymentTestLink(amountOfMugs: Int, sessionId: String, email: String = "") : String {
    val baseLink = when (amountOfMugs) {
        1 -> "https://buy.stripe.com/test_aEU8yDeis99s9ZS146"
        2 -> "https://buy.stripe.com/test_dR62af3DOfxQ7RK3cf"
        3 -> "https://buy.stripe.com/test_6oEcOT2zKbhAfkc3cg"
        4 -> "https://buy.stripe.com/test_dR6g158Y8clEfkc3ch"
        5 -> "https://buy.stripe.com/test_5kA4in1vGdpIc809AG"
        6 -> "https://buy.stripe.com/test_14kaGLcaketMc804gn"
        7 -> "https://buy.stripe.com/test_8wM8yD6Q0fxQ0pi4go"
        8 -> "https://buy.stripe.com/test_cN2bKP3DO2L40pi5kt"
        9 -> "https://buy.stripe.com/test_8wMdSXeis2L43Bu4gq"
        10 -> "https://buy.stripe.com/test_aEU5mr3DO3P88VO9AL"
        else -> return ""
    }
    return "$baseLink?locale=en&$paramSessionId=$sessionId" + if (email.isNotBlank()) "&$prefilledEmail=$email" else email
}

/**
 * @param amountOfMugs to calculate price
 */
fun getCheckoutAmount(amountOfMugs: Int) : Float {
    return (getTotalProductPrice(amountOfMugs) + getTotalShippingPrice(amountOfMugs)).roundTwoDecimals()
}

/**
 * @param amountOfMugs to calculate price
 */
fun getTotalProductPrice(amountOfMugs: Int) : Float {
    return (
        (amountOfMugs*6f)*1.2f// to account for VAT
    ).roundTwoDecimals()
}

/**
 * @param amountOfMugs to calculate price
 */
fun getTotalShippingPrice(amountOfMugs: Int) : Float {
    return (
        (if (amountOfMugs>1) {
            5f + (amountOfMugs - 1)*1.6f
        } else {
            amountOfMugs * 5f
        })*1.2f // to account for VAT
    ).roundTwoDecimals()
}

fun Float.roundTwoDecimals() : Float {
    return (this*100f).roundToInt()/100f
}