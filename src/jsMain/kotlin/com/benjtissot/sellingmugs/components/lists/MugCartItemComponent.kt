package com.benjtissot.sellingmugs.components.lists

import com.benjtissot.sellingmugs.*
import csstype.*
import emotion.react.css
import mui.icons.material.Add
import mui.icons.material.Remove
import mui.material.IconButton
import react.FC
import react.Props
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.img
import kotlin.math.roundToInt


external interface MugCartItemProps: Props {
    var mugCartItem: MugCartItem
    var onChangeQuantity: ((MugCartItem, Int) -> Unit)? // Change mugCartItem quantity by Int value
    var onRemove: ((MugCartItem) -> Unit)?
}

val MugCartItemComponent = FC<MugCartItemProps> { props ->
    div {
        css {
            divDefaultHorizontalCss()
            justifySpaceBetween()
            display = Display.flex
            alignItems = AlignItems.center
            padding = 16.px
            borderBottom = 1.px
        }

        // Image and Name
        div {
            css {
                display = Display.flex
                flexDirection = FlexDirection.row
                alignItems = AlignItems.center
                justifyContent = JustifyContent.center
            }
            // Product image
            img {
                src = props.mugCartItem.mug.getBestPictureSrc()
                // Styles for the product image
                css {
                    width = 80.px
                    height = 80.px
                    marginRight = 16.px
                }
            }

            div {
                // Styles for the product name
                css {
                    fontSize = 2.vh
                    fontWeight = FontWeight.bold
                    marginBottom = 8.px
                }
                +props.mugCartItem.mug.name
            }
        }

        // Quantity, price, remove button
        div {
            css {
                display = Display.flex
                flexDirection = FlexDirection.row
                alignItems = AlignItems.center
                justifyContent = JustifyContent.spaceBetween
                width = 30.vw
                maxWidth = 30.rem
            }

            div {
                // Styles for the product price
                divDefaultCss()
                +"£ ${((props.mugCartItem.mug.price*100f).roundToInt())/100f}"
            }
            div {
                // Styles for the product quantity
                css {
                    fontNormal()
                    color = NamedColor.gray
                }
                props.onChangeQuantity?.let { onChangeQuantity ->
                    IconButton {
                        css { marginRight = 2.vw }
                        Add()
                        onClick = {
                            onChangeQuantity(props.mugCartItem, 1)
                        }
                    }

                    +"Quantity: ${props.mugCartItem.amount}"

                    IconButton {
                        css { marginLeft = 2.vw }
                        Remove()
                        onClick = {
                            onChangeQuantity(props.mugCartItem,  - 1)
                        }
                    }
                } ?: let {
                    +"Quantity: ${props.mugCartItem.amount}"
                }
            }

            // Allows us to display a cart simply for information purposes
            props.onRemove?.let {
                button {
                    // Styles for the remove button
                    divDefaultCss()
                    onClick = { it(props.mugCartItem) }
                    +"Remove"
                }
            }

        }


    }
}