package com.benjtissot.sellingmugs.pages

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.LoadingComponent
import com.benjtissot.sellingmugs.entities.Cart
import com.benjtissot.sellingmugs.entities.printify.order.AddressTo
import com.benjtissot.sellingmugs.entities.printify.order.Order
import com.benjtissot.sellingmugs.entities.printify.order.PrintifyOrderPushResult
import com.benjtissot.sellingmugs.entities.stripe.*
import csstype.vh
import csstype.vw
import emotion.react.css
import io.ktor.client.call.*
import io.ktor.http.*
import io.ktor.util.logging.*
import kotlinx.browser.window
import kotlinx.coroutines.launch
import kotlinx.js.timers.setInterval
import mui.icons.material.Payment
import mui.material.Backdrop
import mui.material.IconButton
import react.FC
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.iframe
import react.router.useNavigate
import react.useEffect
import react.useEffectOnce
import react.useState

private val LOG = KtorSimpleLogger("CheckoutPage.kt")

val CheckoutPage = FC<NavigationProps> { props ->

    var cart: Cart? by useState(null)
    var orderPushResult: PrintifyOrderPushResult? by useState(null)
    var paymentPageOpened by useState(false)


    useEffectOnce {
        scope.launch {
            val cartTemp = getCart()
            cart = cartTemp
        }
    }

    cart?.let {
        /* TODO: infos to give
        * test vs actual payment
        * loading + update when payment has gone through
        */


        val amountOfMugs = it.mugCartItemList.sumOf { item -> item.amount }
        div {
            +"Total product price (with VAT): ${getTotalProductPrice(amountOfMugs)}"
        }
        div {
            +"Total shipping price (with VAT): ${getTotalShippingPrice(amountOfMugs)}"
        }

        IconButton {
            Payment()
            div {
                +"Pay £${getCheckoutAmount(amountOfMugs)} with Stripe"
            }
            onClick = {
                paymentPageOpened = true
                window.open(getPaymentLink(amountOfMugs, props.session.id, props.session.user?.email ?: ""), "_blank")
            }
            formTarget = "_blank"
        }
    }

    if (paymentPageOpened){
        useEffect {
            // Every 2 seconds, check for the order result to be saved in the backend
            setInterval({
                scope.launch {
                    orderPushResult = getOrderPushResultByCartId(cart!!.id)
                }
            }, 2000)

        }

        if (orderPushResult == null){
            LoadingComponent {
                open = paymentPageOpened && orderPushResult == null
            }
        } else {
            div {
                +"The push result has successfully been saved"
            }
        }
    }




}