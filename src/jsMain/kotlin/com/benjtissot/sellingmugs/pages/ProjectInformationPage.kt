package com.benjtissot.sellingmugs.pages

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.highLevel.LoadingComponent
import com.benjtissot.sellingmugs.components.popups.ConfirmCheckoutPopup
import com.benjtissot.sellingmugs.entities.Cart
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
import mui.icons.material.Payment
import mui.material.Box
import mui.material.IconButton
import org.w3c.dom.HTMLButtonElement
import react.*
import react.dom.html.ReactHTML.div

private val LOG = KtorSimpleLogger("CheckoutPage.kt")

val ProjectInformationPage = FC<NavigationProps> { props ->

    useEffectOnce {
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
}
