package com.benjtissot.sellingmugs.pages

import com.benjtissot.sellingmugs.*
import csstype.*
import emotion.react.css
import io.ktor.util.logging.*
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
