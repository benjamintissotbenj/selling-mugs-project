package com.benjtissot.sellingmugs.pages

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.entities.printify.order.AddressTo
import com.benjtissot.sellingmugs.entities.printify.order.Order
import com.benjtissot.sellingmugs.entities.stripe.getPaymentLink
import com.benjtissot.sellingmugs.entities.stripe.paramSessionId
import csstype.vh
import csstype.vw
import emotion.react.css
import io.ktor.client.call.*
import io.ktor.http.*
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

    var order : Order? by useState(null)
    val paymentLink : String = order?.let{ // details of the order helps identify which link to use
        getPaymentLink(1, props.session.id)
    } ?: ""

    useEffectOnce {
        scope.launch {
            // Create order from back-end
            val addressTo = AddressTo(
                "Test",
                "TEST",
                "selling.mugs.imperial@gmail.com",
                "",
                "GB",
                "England",
                "Exhibition Rd",
                "South Kensington",
                "London",
                "SW7 2BX"
            )
            val response = createOrder(addressTo)
            when (response.status) {
                HttpStatusCode.OK -> {
                    order = response.body()
                    props.setAlert(infoAlert("Order was successfully created !"))
                }
                HttpStatusCode.InternalServerError -> props.setAlert(errorAlert("You are not authenticated."))
                else -> props.setAlert(errorAlert("Something went wrong."))
            }

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