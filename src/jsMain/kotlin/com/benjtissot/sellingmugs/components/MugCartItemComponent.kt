package com.benjtissot.sellingmugs.components

import com.benjtissot.sellingmugs.MugCartItem
import com.benjtissot.sellingmugs.divDefaultCss
import com.benjtissot.sellingmugs.divDefaultHorizontalCss
import com.benjtissot.sellingmugs.entities.Mug
import com.benjtissot.sellingmugs.justifySpaceBetween
import csstype.*
import emotion.react.css
import react.FC
import react.Props
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.img
import react.dom.html.ReactHTML.span
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

        // Product image
        div {
            img {
                src = props.mugCartItem.mug.artwork.imageURL
                // Styles for the product image
                css {
                    width = 80.px
                    height = 80.px
                    marginRight = 16.px
                }
            }

            span {
                // Styles for the product name
                css {
                    fontSize = 2.vh
                    fontWeight = FontWeight.bold
                    marginBottom = 8.px
                }
                +props.mugCartItem.mug.name
            }
        }

        span {
            // Styles for the product price
            css {
                marginBottom = 8.px
            }
            +"£ ${props.mugCartItem.mug.price.roundToInt()}"
        }
        span {
            // Styles for the product quantity
            css {
                color = NamedColor.gray
            }
            +"Quantity: ${props.mugCartItem.amount}"
        }
        button {
            // Styles for the remove button
            css {
                marginTop = 8.px
            }
            onClick = { props.onRemove(props.mugCartItem) }
            +"Remove"
        }

        div {
            css {
                alignSelf = AlignSelf.flexEnd
                fontSize = 2.vh
            }
            +"x${props.mugCartItem.amount}"
        }
    }
}