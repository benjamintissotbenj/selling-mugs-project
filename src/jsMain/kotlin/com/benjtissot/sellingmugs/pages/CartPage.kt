package com.benjtissot.sellingmugs.pages

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.FooterComponent
import com.benjtissot.sellingmugs.components.NavigationBarComponent
import io.ktor.util.logging.*
import react.FC
import react.dom.html.ReactHTML.div
import react.router.useNavigate
import react.useEffectOnce
import react.useState

private val LOG = KtorSimpleLogger("CartPage.kt")

external interface CartPageProps : SessionPageProps {
}

val CartPage = FC<CartPageProps> { props ->

    val navigateCart = useNavigate()
    NavigationBarComponent {
        session = props.session
        updateSession = props.updateSession
        navigate = navigateCart
    }

    var message by useState("")

    useEffectOnce {
    }

    div {
        divDefaultCss()
        +"Hello Cart Component"
    }


    FooterComponent {}
}
