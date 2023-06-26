package com.benjtissot.sellingmugs.pages

import com.benjtissot.sellingmugs.NavigationProps
import com.benjtissot.sellingmugs.components.highLevel.LoadingComponent
import com.benjtissot.sellingmugs.entities.Cart
import com.benjtissot.sellingmugs.entities.printify.order.PrintifyOrderPushFail
import com.benjtissot.sellingmugs.entities.printify.order.PrintifyOrderPushResult
import com.benjtissot.sellingmugs.entities.stripe.getCheckoutAmount
import com.benjtissot.sellingmugs.entities.stripe.getPaymentLink
import com.benjtissot.sellingmugs.entities.stripe.getTotalProductPrice
import com.benjtissot.sellingmugs.entities.stripe.getTotalShippingPrice
import com.benjtissot.sellingmugs.getCart
import com.benjtissot.sellingmugs.getOrderPushResultByCartId
import com.benjtissot.sellingmugs.scope
import io.ktor.util.logging.*
import kotlinx.browser.window
import kotlinx.coroutines.launch
import kotlinx.js.timers.Timeout
import kotlinx.js.timers.clearInterval
import kotlinx.js.timers.setInterval
import mui.icons.material.Check
import mui.icons.material.Payment
import mui.material.IconButton
import react.FC
import react.dom.html.ReactHTML.div
import react.useEffect
import react.useEffectOnce
import react.useState

private val LOG = KtorSimpleLogger("CheckoutPage.kt")

val CheckoutPage = FC<NavigationProps> { props ->

    var cart: Cart? by useState(null)
    var orderPushResult: PrintifyOrderPushResult? by useState(null)
    var paymentPageOpened by useState(false)
    var getOrderPushResultTimeout: Timeout? = null

    useEffectOnce {
        scope.launch {
            val cartTemp = getCart()
            // in case there was a refresh, still show the information about the order being pushed
            orderPushResult = getOrderPushResultByCartId(cartTemp.id)
            cart = cartTemp
        }
    }

    useEffect {
        if (paymentPageOpened) {
            if (orderPushResult == null){
                // Every 2 seconds, check for the order result to be saved in the backend
                getOrderPushResultTimeout = setInterval({
                    scope.launch {
                        val pushResultTemp = getOrderPushResultByCartId(cart!!.id)
                        if (pushResultTemp != null) {
                            getOrderPushResultTimeout?.let { clearInterval(it) }
                            // TODO check timing on this
                            props.updateSession()
                            orderPushResult = pushResultTemp
                        }
                    }
                }, 2000)
            } else {
                getOrderPushResultTimeout?.let { clearInterval(it) }
            }

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

        IconButton {
            Check()
            +"I have paid"
            onClick = {
                scope.launch {
                    orderPushResult = getOrderPushResultByCartId(cart!!.id)
                }
            }
        }

        if (orderPushResult == null){
            LoadingComponent {
                open = paymentPageOpened && orderPushResult == null
            }
        } else {
            // Check result + show alert when issue pushing
            if (orderPushResult is PrintifyOrderPushFail) {
                div {
                    +"The order failed"
                }
                div {
                    +((orderPushResult as PrintifyOrderPushFail).message)
                }
            }
            div {
                +"The push result has successfully been saved"
            }
        }
    }




}