package com.benjtissot.sellingmugs.pages

import com.benjtissot.sellingmugs.NavigationProps
import com.benjtissot.sellingmugs.SessionPageProps
import com.benjtissot.sellingmugs.entities.printify.order.Order
import com.benjtissot.sellingmugs.mainPageDiv
import com.benjtissot.sellingmugs.scope
import csstype.vh
import csstype.vw
import emotion.react.css
import io.ktor.util.logging.*
import kotlinx.browser.window
import kotlinx.coroutines.launch
import mui.icons.material.Payment
import mui.material.IconButton
import react.FC
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.iframe
import react.router.useNavigate
import react.useEffectOnce
import react.useState

private val LOG = KtorSimpleLogger("CheckoutPage.kt")

val CheckoutPage = FC<NavigationProps> { props ->

    val order : Order? by useState(null)
    val paymentLink : String = order?.let{"https://buy.stripe.com/3cseWd3FX7uVc6c3ce?client_reference_id=${it.external_id}"} ?: ""

    useEffectOnce {
        scope.launch {
            // Create order from back-end

            // When back-end receives web-hook of order paid, order is sent to production in Printify


        }
    }

    div {
        +"Hello Checkout Component"
    }


    IconButton {
        Payment()
        div {
            +"Pay with Stripe"
        }
        onClick = {
            window.open(paymentLink, "_blank")
        }
        formTarget = "_blank"
    }




}