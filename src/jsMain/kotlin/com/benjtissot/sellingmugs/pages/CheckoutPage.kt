package com.benjtissot.sellingmugs.pages

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.highLevel.LoadingComponent
import com.benjtissot.sellingmugs.entities.Cart
import com.benjtissot.sellingmugs.entities.printify.order.PrintifyOrderPushFail
import com.benjtissot.sellingmugs.entities.printify.order.PrintifyOrderPushResult
import com.benjtissot.sellingmugs.entities.printify.order.PrintifyOrderPushSuccess
import com.benjtissot.sellingmugs.entities.stripe.*
import csstype.*
import emotion.react.css
import io.ktor.util.logging.*
import kotlinx.browser.window
import kotlinx.coroutines.launch
import kotlinx.js.timers.Timeout
import kotlinx.js.timers.clearInterval
import kotlinx.js.timers.setInterval
import mui.icons.material.Check
import mui.icons.material.Payment
import mui.material.Box
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

    div {
        css {
            display = Display.flex
            flexDirection = FlexDirection.column
            alignItems = AlignItems.center
            width = 100.pct
            height = 100.pct
            boxSizing = BoxSizing.borderBox
        }
        cart?.let {
            /* TODO: infos to give
            * test vs actual payment
            */

            Box {
                css {
                    backgroundColor = Color(Const.ColorCode.LIGHT_BLUE.code())
                    borderRadius = 2.vw
                    borderColor = Color(Const.ColorCode.BLUE.code())
                    margin = 5.vw
                    padding = 5.vw
                    width = 80.pct
                    height = 50.pct
                    boxSizing = BoxSizing.borderBox
                }
                div {
                    css {
                        display = Display.flex
                        flexDirection = FlexDirection.column
                        alignItems = AlignItems.center
                        width = 100.pct
                        height = 100.pct
                        boxSizing = BoxSizing.borderBox
                    }
                    div {
                        css {
                            fontNormalPlus()
                            fontWeight = FontWeight.bold
                        }
                        +"Disclaimer"
                    }
                    div {
                        css {
                            fontNormal()
                            padding = 3.vw
                            width = 100.pct
                            height = 100.pct
                            boxSizing = BoxSizing.borderBox
                        }
                        val disclaimerMessage = "This website is part of a MSc Project for Imperial College. " +
                                "As such, you have the possibility to create a test order to help with the " +
                                "research, or to checkout a real order that will result in a mug being delivered. " +
                                "Please bear in mind that only addresses in England will be accepted for delivery."
                        +disclaimerMessage
                    }
                }

            }

            val amountOfMugs = it.mugCartItemList.sumOf { item -> item.amount }

            div {
                css {
                    display = Display.flex
                    flexDirection = FlexDirection.row
                    alignItems = AlignItems.start
                    justifyContent = JustifyContent.spaceBetween
                    width = 100.pct
                    boxSizing = BoxSizing.borderBox
                    paddingLeft = 10.vw
                    paddingRight = 10.vw
                    paddingTop = 5.vw
                }
                div {
                    css {
                        display = Display.flex
                        flexDirection = FlexDirection.column
                        alignItems = AlignItems.start
                    }
                    div {
                        +"Total product price (with VAT): £${getTotalProductPrice(amountOfMugs)}"
                    }
                    div {
                        +"Total shipping price (with VAT): £${getTotalShippingPrice(amountOfMugs)}"
                    }
                }

                div {
                    css {
                        display = Display.flex
                        flexDirection = FlexDirection.column
                        alignItems = AlignItems.start
                    }
                    // Test pay
                    IconButton {
                        disabled = (amountOfMugs == 0)
                        Payment()
                        div {
                            +"Test Pay £${getCheckoutAmount(amountOfMugs)} with Stripe"
                        }
                        onClick = {
                            paymentPageOpened = true
                            window.open(
                                getPaymentTestLink(amountOfMugs, props.session.id, props.session.user?.email ?: ""),
                                "_blank"
                            )
                        }
                        formTarget = "_blank"
                    }
                    // Real pay
                    IconButton {
                        disabled = true // todo: implement
                        Payment()
                        div {
                            +"Pay £${getCheckoutAmount(amountOfMugs)} with Stripe"
                        }
                        onClick = {
                            paymentPageOpened = true
                            window.open(
                                getPaymentLink(amountOfMugs, props.session.id, props.session.user?.email ?: ""),
                                "_blank"
                            )
                        }
                        formTarget = "_blank"
                    }
                }
            }
        }

        if (paymentPageOpened) {
            when (orderPushResult) {
                null -> LoadingComponent {
                    open = paymentPageOpened && orderPushResult == null
                }
                is PrintifyOrderPushFail -> div {
                    +"The order failed with message ${(orderPushResult as PrintifyOrderPushFail).message}"
                }
                is PrintifyOrderPushSuccess -> div {
                    +"The push result has successfully been saved"
                }
            }
        }
    }
}