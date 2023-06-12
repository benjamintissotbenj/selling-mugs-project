package com.benjtissot.sellingmugs.pages

import com.benjtissot.sellingmugs.SessionPageProps
import com.benjtissot.sellingmugs.mainPageDiv
import emotion.react.css
import io.ktor.util.logging.*
import react.FC
import react.dom.html.ReactHTML.div
import react.router.useNavigate

private val LOG = KtorSimpleLogger("CheckoutPage.kt")

external interface CheckoutPageProps: SessionPageProps {
}

val CheckoutPage = FC<SessionPageProps> { props ->
    val navigateCheckout = useNavigate()

    div {
        css {
            mainPageDiv()
        }
        div {
            +"Hello Checkout Component"
        }
    }

}