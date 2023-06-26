package com.benjtissot.sellingmugs.components.lists

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.entities.printify.order.Order
import com.benjtissot.sellingmugs.entities.printify.toPrettyFormat
import com.benjtissot.sellingmugs.entities.stripe.getCheckoutAmount
import csstype.*
import emotion.react.css
import kotlinx.coroutines.launch
import mui.icons.material.ArrowDropDown
import mui.icons.material.ArrowDropUp
import mui.material.Card
import mui.material.IconButton
import mui.material.Size
import react.FC
import react.Props
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.useState


external interface UserOrderItemProps: Props {
    var order: Order
    var onClickCancel: (Order) -> Unit
}

val UserOrderItemComponent = FC<UserOrderItemProps> { props ->

    var expanded by useState(false)
    var mugCartItemsFromOrder : List<MugCartItem> by useState(emptyList())

    div {
        css {
            card()
            marginTop = 1.vh
            marginBottom = 1.vh
        }

        // Main info
        div {
            css {
                cardTopHalf()
            }
            // Label
            div {
                css {
                    fontNormal()
                    padding = 16.px
                    marginLeft = 2.vw
                    fontWeight = FontWeight.bold
                }
                +"Order ${props.order.label}"
            }

            // Creation time
            div {
                css {
                    fontNormal()
                    padding = 16.px
                }
                +"Order made on ${props.order.created_at.toPrettyFormat()}"
            }

            // Status
            div {
                css {
                    fontNormal()
                    padding = 16.px
                }
                +props.order.status
            }

            // Total price
            div {
                css {
                    fontNormal()
                    padding = 16.px
                }
                +"£${getCheckoutAmount(props.order.line_items.size)}"
            }

            // Cancel order
            button {
                css {
                    fontNormal()
                    padding = 16.px
                    marginRight = 2.vw
                }
                disabled = props.order.status != Order.STATUS_ON_HOLD
                onClick = { props.onClickCancel(props.order) }
                +"Cancel"
            }


            // Show Details
            IconButton {
                css {
                    display = Display.flex
                    flexDirection = FlexDirection.row
                    alignItems = AlignItems.center
                }
                size = Size.small
                if (expanded) {
                    div {
                        css {
                            marginRight = 1.vw
                        }
                        +"Hide details"
                    }
                    ArrowDropUp()
                    onClick = {
                        expanded = false
                    }
                } else {
                    div {
                        css {
                            marginRight = 1.vw
                        }
                        +"Show details"
                    }
                    ArrowDropDown()
                    onClick = {
                        if (mugCartItemsFromOrder.isEmpty()){
                            scope.launch {
                                mugCartItemsFromOrder = getOrderLineItemsAsMugCartItems(props.order.external_id)
                            }
                        }
                        expanded = true
                    }
                }


            }
        }

        // If Show Details is clicked, expand the card
        if (expanded) {
            div {
                css {
                    cardBottomHalf()
                }
                if (mugCartItemsFromOrder.isEmpty()) {
                    // If data is not yet loaded
                    +"Data is loading, please wait."
                }
                div {
                    css {
                        display = Display.flex
                        flexDirection = FlexDirection.column
                        overflowY = "auto".unsafeCast<Overflow>()
                        scrollBehavior = ScrollBehavior.smooth
                        paddingBlock = 1.rem
                        boxSizing = BoxSizing.borderBox
                        width = 100.pct
                        maxHeight = 40.vh
                    }
                    // For each, display information
                    mugCartItemsFromOrder.forEach { mugCartItem ->
                        MugCartItemComponent {
                            this.mugCartItem = mugCartItem
                            onRemove = null // Ensures there is no "delete" button
                        }
                    }
                }
            }
        }
    }
}