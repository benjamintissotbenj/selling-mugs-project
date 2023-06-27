package com.benjtissot.sellingmugs.pages

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.lists.CartListComponent
import com.benjtissot.sellingmugs.entities.Cart
import com.benjtissot.sellingmugs.entities.stripe.getTotalProductPrice
import com.benjtissot.sellingmugs.entities.stripe.getTotalShippingPrice
import csstype.*
import emotion.css.css
import emotion.react.css
import io.ktor.util.logging.*
import kotlinx.coroutines.launch
import mui.icons.material.Payment
import mui.material.Button
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
            if (amountOfMugs > 0) {
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
                        IconButton {
                            div {
                                css {
                                    fontBig()
                                    marginRight = 5.vw
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
            } else {
                css {
                    fontNormal()
                    display = Display.flex
                    flexDirection = FlexDirection.column
                    alignItems = AlignItems.center
                    padding = 10.vw
                }
                div {+"It looks like you haven't added anything to your cart."}
                Button {
                    css {
                        marginTop = 5.vh
                    }
                    +"See available mugs"
                    onClick = {
                        props.navigate.invoke(HOMEPAGE_PATH)
                    }
                }
            }
        }

    }

}
