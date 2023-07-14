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
                    marginBlock = 5.vh
                    width = 100.pct
                    height = 10.pct
                    display = Display.flex
                    flexDirection = FlexDirection.column
                    alignItems = AlignItems.center
                }
                +"Selling Mugs Project - Information"
            }

            div {
                css {
                    fontNormal()
                    padding = 3.vh
                    width = 80.pct
                    height = 40.pct
                    boxSizing = BoxSizing.borderBox
                }
                +Const.projectDescriptionMessage
            }
        }

    }
}
