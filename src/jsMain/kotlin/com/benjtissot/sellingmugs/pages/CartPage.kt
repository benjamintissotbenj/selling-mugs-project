package com.benjtissot.sellingmugs.pages

import com.benjtissot.sellingmugs.SessionPageProps
import com.benjtissot.sellingmugs.components.FooterComponent
import com.benjtissot.sellingmugs.components.NavigationBarComponent
import com.benjtissot.sellingmugs.divDefaultCss
import com.benjtissot.sellingmugs.entities.Cart
import com.benjtissot.sellingmugs.getCart
import com.benjtissot.sellingmugs.scope
import io.ktor.util.logging.*
import kotlinx.coroutines.launch
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

    var cart: Cart? by useState(null)

    useEffectOnce {
        scope.launch {
            cart = getCart()
        }
    }

    div {
        divDefaultCss()
        +"Hello Cart Component"
    }
    div {
        divDefaultCss()
        +"The cart is ${cart.toString()}"
    }

    FooterComponent {}
}
