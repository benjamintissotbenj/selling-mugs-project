package com.benjtissot.sellingmugs.pages

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.lists.CartListComponent
import com.benjtissot.sellingmugs.entities.local.Cart
import csstype.*
import emotion.react.css
import io.ktor.http.*
import io.ktor.util.logging.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mui.icons.material.Payment
import mui.icons.material.Save
import mui.material.Button
import mui.material.IconButton
import react.FC
import react.dom.html.ReactHTML.div
import react.useEffectOnce
import react.useState

private val LOG = KtorSimpleLogger("CartPage.kt")

val CartPage = FC<NavigationProps> { props ->

    var cart: Cart? by useState(null)
    var savedCart: Cart? by useState(null)

    useEffectOnce {
        scope.launch {
            cart = getCart()
            props.session.user?.let {savedCart = getSavedCart(it.id)}
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
                    onChangeQuantity = { mugCartItem, deltaQuantity ->
                        scope.launch {
                            changeMugCartItemQuantity(mugCartItem, deltaQuantity)
                            cart = getCart()
                            delay(50L)
                            props.updateSession()
                        }
                    }
                    onRemoveItem = { mugCartItem ->
                        scope.launch {
                            removeMugCartItemFromCart(mugCartItem)
                            cart = getCart()
                            delay(50L)
                            props.updateSession()
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

                    // Checkout Button
                    IconButton {
                        disabled = (amountOfMugs > 10)
                        css {
                            fontBig()
                            marginRight = 3.vw
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

                    // Save cart button
                    if (props.session.user != null) {
                        IconButton {
                            css {
                                fontBig()
                                marginRight = 3.vw
                            }
                            div {
                                css {
                                    marginRight = 1.vw
                                }
                                +"Save your cart for another day"
                            }
                            Save()
                            onClick = {
                                props.setAlert(infoAlert("Saving cart..."))
                                scope.launch {
                                    recordClick(props.session.clickDataId, Const.ClickType.CART_SAVE_TO_USER.toString())
                                    when (saveCartToUser(props.session.user!!.id)){
                                        HttpStatusCode.OK -> {
                                            props.setAlert(successAlert("Cart saved successfully !"))
                                            props.session.user?.let { user -> savedCart = getSavedCart(user.id)}
                                        }
                                        else -> props.setAlert(errorAlert("Unable to save cart."))
                                    }
                                }
                            }
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

                // Suggest to see available mugs
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

            // If a logged in has a saved cart, button to load the saved cart
            savedCart?.let {
                LOG.debug("\nCurrent Cart is $cart, \n saved cart is $savedCart and they are different : ${savedCart!! != cart}")
            }
            if (savedCart != null && savedCart!! != cart) {
                // Adapt the messages to the amount of mugs
                if (amountOfMugs > 0) {
                    div {+"You also have a cart saved."}
                } else {
                    div {+"However, you have a cart saved."}
                }
                Button {
                    css {
                        marginTop = 5.vh
                    }
                    +"Load your saved cart ${if (amountOfMugs>0) "instead" else ""}"
                    onClick = {
                        scope.launch {
                            recordClick(props.session.clickDataId, Const.ClickType.CART_LOAD_SAVED_CART.type)
                            loadSavedCart()
                            cart = getCart()
                            delay(100L)
                            props.updateSession()
                        }
                    }
                }
            }
        }
    }
}
