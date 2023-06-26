package com.benjtissot.sellingmugs.components.lists

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.entities.printify.order.Order
import com.benjtissot.sellingmugs.entities.printify.order.PrintifyOrderPushFail
import com.benjtissot.sellingmugs.entities.printify.order.StoredOrderPushFailed
import csstype.*
import emotion.react.css
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.monthsUntil
import mui.material.MenuItem
import mui.material.Select
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.header
import react.useEffectOnce
import react.useState


external interface UserOrderListProps: Props {
    var userId: String
    var setAlert: (AlertState) -> Unit
}

val UserOrderListComponent = FC<UserOrderListProps> { props ->

    var orderList : List<Order>? by useState(null)
    var orderPushFails : List<StoredOrderPushFailed> by useState(emptyList())
    var filterSelector : String by useState(Const.ORDER_FILTER_THREE_MONTHS)

    useEffectOnce {
        scope.launch {
            orderList = getUserOrderList(props.userId)
            orderPushFails = getOrderPushFailsByUser(props.userId)
        }
    }

    if (orderList.isNullOrEmpty()){
        div {
            css {
                contentCenteredVertically()
                contentCenteredHorizontally()
            }
            if (orderList == null){
                +"Data is loading, please wait."
            } else {
                +"You haven't ordered anything yet !"
            }
        }
    } else {
        orderPushFails.forEach { orderPushFail ->
            div {
                css {
                    card()
                    backgroundColor = NamedColor.red
                }
                +"Order Id is ${orderPushFail.orderId} and Message is ${orderPushFail.printifyOrderPushFail.message}"
            }
        }


        header {
            css {
                width = 100.pct
                height = 4.pct
                display = Display.flex
                justifyContent = JustifyContent.spaceBetween
                alignItems = AlignItems.center
            }
            div {
                css {
                    fontNormalPlus()
                    marginLeft = 10.vw
                }
                +"Your orders"
            }

            div {
                css {
                    display = Display.flex
                    flexDirection = FlexDirection.row
                    marginRight = 5.vw
                }
                div {
                    css {
                        contentCenteredVertically()
                        marginRight = 3.vh
                    }
                    +"See orders :"
                }

                Select {
                    // Attributes
                    css {
                        width = 20.vw
                        height = 5.vh
                        color = NamedColor.white
                        fontNormal()
                    }

                    value = filterSelector
                    onChange = { event, _ ->
                        filterSelector = event.target.value
                    }


                    // Children, in the selector

                    MenuItem {
                        value = Const.ORDER_FILTER_THREE_MONTHS
                        +Const.ORDER_FILTER_THREE_MONTHS
                    }
                    MenuItem {
                        value = Const.ORDER_FILTER_SIX_MONTHS
                        +Const.ORDER_FILTER_SIX_MONTHS
                    }
                    MenuItem {
                        value = Const.ORDER_FILTER_ALL
                        +Const.ORDER_FILTER_ALL
                    }
                }
            }

        }
        div {
            css {
                display = Display.flex
                flexDirection = FlexDirection.column
                overflowY = "auto".unsafeCast<Overflow>()
                scrollBehavior = ScrollBehavior.smooth
                paddingBlock = 1.vh
                boxSizing = BoxSizing.borderBox
                width = 100.pct
                height = 95.pct
            }
            orderList!!.filter {order ->
                when (filterSelector){
                    Const.ORDER_FILTER_ALL -> true
                    Const.ORDER_FILTER_THREE_MONTHS -> order.created_at.monthsUntil(Clock.System.now(), TimeZone.currentSystemDefault()) <= 3
                    Const.ORDER_FILTER_SIX_MONTHS -> order.created_at.monthsUntil(Clock.System.now(), TimeZone.currentSystemDefault()) <= 6
                    else -> false
                }
            }.forEach { order ->
                UserOrderItemComponent {
                    this.order = order
                    onClickCancel = { order ->
                        scope.launch {
                            val statusCode = cancelOrder(order.external_id)
                            when (statusCode.value) {
                                6 -> props.setAlert(errorAlert("Order was not found"))
                                200 -> props.setAlert(successAlert("Order was cancelled successfully !"))
                                else -> props.setAlert(errorAlert("Something went wrong"))
                            }
                            orderList = getUserOrderList(props.userId)
                        }

                    }
                }
            }
        }
    }
}