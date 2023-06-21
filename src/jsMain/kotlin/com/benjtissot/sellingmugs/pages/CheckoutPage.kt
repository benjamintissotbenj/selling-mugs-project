package com.benjtissot.sellingmugs.pages

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.entities.Cart
import com.benjtissot.sellingmugs.entities.printify.order.AddressTo
import com.benjtissot.sellingmugs.entities.printify.order.Order
import com.benjtissot.sellingmugs.entities.stripe.*
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

    var cart: Cart? by useState(null)

    useEffectOnce {
        scope.launch {
            cart = getCart()
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
                window.open(getPaymentLink(amountOfMugs, props.session.id, props.session.user?.email ?: ""), "_blank")
            }
            formTarget = "_blank"
        }
    }




}