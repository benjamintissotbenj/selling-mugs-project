package com.benjtissot.sellingmugs.entities.stripe

val paramSessionId = "client_reference_id"


fun getPaymentLink(amountOfMugs: Int, sessionId: String) : String {
    val baseLink = when (amountOfMugs) {
        else -> "https://buy.stripe.com/test_28odSX5LW4Tc9ZS9AB"
    }
    return "$baseLink?locale=en&$paramSessionId=$sessionId"
}