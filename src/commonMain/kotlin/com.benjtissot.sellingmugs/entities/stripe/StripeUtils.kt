package com.benjtissot.sellingmugs.entities.stripe

import kotlin.math.roundToInt

val paramSessionId = "client_reference_id"
val prefilledEmail = "prefilled_email"

/**
 * Gets the correct payment link depending on the amount of mugs
 * @param sessionId the id of the current session, to be linked when the payment goes through
 * @param email the user email to be pre-filled in payment page
 */
fun getPaymentLink(amountOfMugs: Int, sessionId: String, email: String = "") : String {
    val baseLink = when (amountOfMugs) {
        1 -> "https://buy.stripe.com/test_aEU8yDeis99s9ZS146"
        2 -> "https://buy.stripe.com/test_dR62af3DOfxQ7RK3cf"
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