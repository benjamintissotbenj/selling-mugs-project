package com.benjtissot.sellingmugs.pages

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.highLevel.LoadingComponent
import com.benjtissot.sellingmugs.components.popups.ConfirmCheckoutPopup
import com.benjtissot.sellingmugs.entities.Cart
import com.benjtissot.sellingmugs.entities.printify.order.Order
import com.benjtissot.sellingmugs.entities.printify.order.PrintifyOrderPushFail
import com.benjtissot.sellingmugs.entities.printify.order.PrintifyOrderPushResult
import com.benjtissot.sellingmugs.entities.printify.order.PrintifyOrderPushSuccess
import com.benjtissot.sellingmugs.entities.stripe.*
import csstype.*
import emotion.react.css
import io.ktor.util.logging.*
import kotlinx.browser.window
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.js.timers.Timeout
import kotlinx.js.timers.clearInterval
import kotlinx.js.timers.setInterval
import mui.icons.material.Check
import mui.icons.material.Payment
import mui.material.Box
import mui.material.Button
import mui.material.IconButton
import mui.material.Popper
import org.w3c.dom.HTMLButtonElement
import react.*
import react.dom.html.ReactHTML.div

private val LOG = KtorSimpleLogger("CheckoutPage.kt")

val CheckoutPage = FC<NavigationProps> { props ->

    var cart: Cart? by useState(null)
    var orderPushResult: PrintifyOrderPushResult? by useState(null)
    var paymentPageOpened by useState(false)
    var getOrderPushResultTimeout: Timeout? = null
    var popupTarget : HTMLButtonElement? by useState(null)


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
                            props.updateSession()
                            when (pushResultTemp) {
                                is PrintifyOrderPushSuccess -> props.setAlert(successAlert("The order has been placed successfully !"))
                                is PrintifyOrderPushFail -> props.setAlert(errorAlert("The order could not be placed : ${pushResultTemp.errors.reason}"))
                                else -> {}
                            }
                            paymentPageOpened = true
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
            Box {
                css {
                    backgroundColor = Color(Const.ColorCode.LIGHT_BLUE.code())
                    borderRadius = 2.vw
                    borderColor = Color(Const.ColorCode.BLUE.code())
                    marginTop = 5.vw
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
                    // Warning about ordering less than 10 mugs
                    if (amountOfMugs > 10) {
                        div {
                            css {
                                color = NamedColor.red
                                paddingBlock = 1.vh
                                fontSmall()
                            }
                            +"You can only buy a maximum of 10 mugs at a time"
                        }
                    } else {
                        // Test pay
                        IconButton {
                            disabled = (amountOfMugs == 0)
                            Payment()
                            div {
                                +"Test Pay £${getCheckoutAmount(amountOfMugs)} with Stripe"
                            }
                            onClick = {
                                scope.launch {
                                    recordClick(props.session.clickDataId, Const.ClickType.TEST_PAY.type)
                                }
                                paymentPageOpened = true
                                scope.launch {
                                    delay(25L)
                                    window.open(
                                        getPaymentTestLink(amountOfMugs, props.session.id, props.session.user?.email ?: ""),
                                        "_blank"
                                    )
                                }
                            }
                        }

                        // Real pay
                        IconButton {
                            disabled = (amountOfMugs == 0)
                            Payment()
                            div {
                                +"Pay £${getCheckoutAmount(amountOfMugs)} with Stripe"
                            }
                            onClick = { event ->
                                popupTarget = event.currentTarget
                            }
                        }

                        ConfirmCheckoutPopup {
                            this.popupTarget = popupTarget
                            this.amountOfMugs = amountOfMugs
                            this.onClickCancel = {
                                popupTarget = null
                            }
                            this.onClickConfirm = {
                                scope.launch {
                                    recordClick(props.session.clickDataId, Const.ClickType.REAL_PAY.type)
                                }
                                paymentPageOpened = true
                                scope.launch {
                                    delay(25L)
                                    window.open(
                                        getPaymentLink(amountOfMugs, props.session.id, props.session.user?.email ?: ""),
                                        "_blank"
                                    )
                                }
                                popupTarget = null
                            }
                        }

                    }
                }
            }
        }

        if (paymentPageOpened) {
            when (orderPushResult) {
                null -> LoadingComponent {
                    open = paymentPageOpened && orderPushResult == null
                    onClickClose = {
                        getOrderPushResultTimeout?.let { clearInterval(it) }
                        paymentPageOpened = false
                    }
                }
                is PrintifyOrderPushFail -> div {
                    +"The order failed with message ${(orderPushResult as PrintifyOrderPushFail).message}. You can edit this in your profile page."
                }
                is PrintifyOrderPushSuccess -> div {
                    +"The order has been placed successfully !"
                }
            }
        }
    }
}
