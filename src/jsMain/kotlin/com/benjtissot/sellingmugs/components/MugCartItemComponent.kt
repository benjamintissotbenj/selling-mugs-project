package com.benjtissot.sellingmugs.components

import com.benjtissot.sellingmugs.*
import csstype.*
import emotion.react.css
import react.FC
import react.Props
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.img
import kotlin.math.roundToInt


external interface MugCartItemProps: Props {
    var mugCartItem: MugCartItem
    var onRemove: (MugCartItem) -> Unit
}

val MugCartItemComponent = FC<MugCartItemProps> {
        props ->
    div {
        css {
            divDefaultHorizontalCss()
            justifySpaceBetween()
            display = Display.flex
            alignItems = AlignItems.center
            padding =16.px
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
                src = props.mugCartItem.mug.artwork.imageURL
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
                +"£ ${props.mugCartItem.mug.price.roundToInt()}"
            }
            div {
                // Styles for the product quantity
                css {
                    fontNormal()
                    color = NamedColor.gray
                }
                +"Quantity: ${props.mugCartItem.amount}"
            }
            button {
                // Styles for the remove button
                divDefaultCss()
                onClick = { props.onRemove(props.mugCartItem) }
                +"Remove"
            }
        }


    }
}