package com.benjtissot.sellingmugs.components.lists

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.entities.printify.order.Order
import com.benjtissot.sellingmugs.entities.printify.toPrettyFormat
import com.benjtissot.sellingmugs.entities.stripe.getCheckoutAmount
import csstype.*
import emotion.react.css
import mui.material.Card
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
    Card {
        css {
            divDefaultHorizontalCss()
            justifySpaceBetween()
            display = Display.flex
            alignItems = AlignItems.center
            padding = 16.px
            borderColor = Color(Const.ColorCode.GREY_DELIMITER.code())
            backgroundColor = Color(Const.ColorCode.BACKGROUND_BLUE_DARK.code())
            marginTop = 1.vh
            marginBottom = 1.vh
        }

        // Label
        div {
            css {
                fontSize = 2.vh
                fontWeight = FontWeight.bold
                marginBottom = 8.px
            }
            +"Order ${props.order.label}"
        }

        // Creation time
        div {
            css {
                fontSize = 2.vh
                marginBottom = 8.px
            }
            +"Order made on ${props.order.created_at.toPrettyFormat()}"
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

            // TODO: button see items

            // Status
            div {
                divDefaultCss()
                +props.order.status
            }

            // Total price
            div {
                divDefaultCss()
                +"£${getCheckoutAmount(props.order.line_items.size)}"
            }

            button {
                // Styles for the remove button
                // TODO : make button disabled if status is not on-hold
                css {
                    fontNormal()
                    marginRight = 2.vw
                }
                onClick = { props.onClickCancel(props.order) }
                +"Cancel"
            }
        }


    }
}