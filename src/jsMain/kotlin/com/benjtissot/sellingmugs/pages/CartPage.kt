package com.benjtissot.sellingmugs.pages

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.CartListComponent
import com.benjtissot.sellingmugs.components.FooterComponent
import com.benjtissot.sellingmugs.components.NavigationBarComponent
import com.benjtissot.sellingmugs.entities.Cart
import csstype.px
import emotion.react.css
import io.ktor.util.logging.*
import kotlinx.coroutines.launch
import mui.icons.material.Payment
import mui.material.IconButton
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
    var cart: Cart? by useState(null)

    useEffectOnce {
        scope.launch {
            cart = getCart()
        }
    }

    NavigationBarComponent {
        session = props.session
        updateSession = props.updateSession
        navigate = navigateCart
    }

    div {
        css {
            mainPageDiv()
        }

        cart?.let{
            CartListComponent{
                title = "Cart"
                list = cart!!.mugCartItemList
                onRemoveItem = { mugCartItem ->
                    scope.launch {
                        removeMugCartItemFromCart(mugCartItem)
                        cart = getCart()
                    }
                }
            }

            IconButton {
                +"Checkout"
                Payment()
            }
        }
    }



    FooterComponent {}
}
