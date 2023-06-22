package com.benjtissot.sellingmugs.pages

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.lists.CartListComponent
import com.benjtissot.sellingmugs.entities.Cart
import com.benjtissot.sellingmugs.entities.stripe.getTotalProductPrice
import com.benjtissot.sellingmugs.entities.stripe.getTotalShippingPrice
import csstype.Display
import csstype.FlexDirection
import csstype.pct
import csstype.px
import emotion.react.css
import io.ktor.util.logging.*
import kotlinx.coroutines.launch
import mui.icons.material.Payment
import mui.material.IconButton
import react.FC
import react.dom.html.ReactHTML.div
import react.useEffectOnce
import react.useState

private val LOG = KtorSimpleLogger("CartPage.kt")

val CartPage = FC<NavigationProps> { props ->

    var cart: Cart? by useState(null)

    useEffectOnce {
        scope.launch {
            cart = getCart()
        }
    }
    cart?.let {
        val amountOfMugs = it.mugCartItemList.sumOf { item -> item.amount }
        div {
            css {
                contentCenteredHorizontally()
            }
            CartListComponent {
                title = "Cart"
                list = cart!!.mugCartItemList
                onRemoveItem = { mugCartItem ->
                    scope.launch {
                        removeMugCartItemFromCart(mugCartItem)
                        cart = getCart()
                    }
                }
            }
            div {
                css {
                    width = 100.pct
                    display = Display.flex
                    flexDirection = FlexDirection.rowReverse
                }
                div {
                    +"Total product price (with VAT): ${getTotalProductPrice(amountOfMugs)}"
                }
                div {
                    +"Total shipping price (with VAT): ${getTotalShippingPrice(amountOfMugs)}"
                }
                IconButton {
                    div {
                        css {
                            fontBig()
                            marginRight = 16.px
                        }
                        +"Checkout"
                    }
                    Payment()
                    onClick = {
                        scope.launch {
                            recordClick(props.session.clickDataId, Const.ClickType.CHECKOUT_CART.toString())
                        }
                        props.navigate.invoke(CHECKOUT_PATH)
                    }
                }
            }
        }

    }

}
