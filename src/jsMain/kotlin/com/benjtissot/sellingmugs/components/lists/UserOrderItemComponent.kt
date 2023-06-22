package com.benjtissot.sellingmugs.components.lists

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.entities.printify.order.Order
import csstype.*
import emotion.react.css
import react.FC
import react.Props
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div


external interface UserOrderItemProps: Props {
    var order: Order
    var onClickCancel: (Order) -> Unit
}

val UserOrderItemComponent = FC<UserOrderItemProps> {
        props ->
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

            div {
                // Styles for the order's label
                css {
                    fontSize = 2.vh
                    fontWeight = FontWeight.bold
                    marginBottom = 8.px
                }
                +props.order.label
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
                +props.order.status
            }
            button {
                // Styles for the remove button
                divDefaultCss()
                onClick = { props.onClickCancel(props.order) }
                +"Cancel"
            }
        }


    }
}