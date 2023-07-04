package com.benjtissot.sellingmugs.pages

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.lists.CartListComponent
import com.benjtissot.sellingmugs.entities.Cart
import csstype.*
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
                        marginRight = 5.vw
                    }
                    IconButton {
                        disabled = (amountOfMugs > 10)
                        css {
                            fontBig()
                        }
                        div {
                            css {
                                marginRight = 1.vw
                            }
                            +"Checkout"
                        }
                        Payment()
                        onClick = {
                            scope.launch {
                                recordClick(props.session.clickDataId, Const.ClickType.CART_CHECKOUT.toString())
                            }
                            props.navigate.invoke(CHECKOUT_PATH)
                        }
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
                        scope.launch {
                            recordClick(props.session.clickDataId, Const.ClickType.CART_SEE_AVAILABLE_MUGS.type)
                        }
                        props.navigate.invoke(HOMEPAGE_PATH)
                    }
                }
            }
        }
    }
}
