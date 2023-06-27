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
import mui.material.*
import mui.system.sx
import org.w3c.dom.HTMLButtonElement
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
    var popupTarget : HTMLButtonElement? by useState(null)
    var orderToCancel : Order? by useState(null)

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
                +"Order ${orderPushFail.orderId} failed because : ${orderPushFail.printifyOrderPushFail.errors.reason}"
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
                paddingTop = 1.vh
                paddingBottom = 5.vh
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
            }.reversed( // We want the items to appear from most recent to oldest
            ).forEach { order ->
                UserOrderItemComponent {
                    this.order = order
                    onClickCancel = { order, popupAnchor ->
                        // TODO: add every payment link
                        orderToCancel = order
                        popupTarget = popupAnchor
                    }
                    cancelling = (order.id == orderToCancel?.id) && order.status != Order.STATUS_CANCELLED
                }
            }
        }

        ConfirmCancelPopup {
            this.popupTarget = popupTarget
            this.orderToCancel = orderToCancel
            onClickCancel = {
                orderToCancel = null
                popupTarget = null
            }
            onClickConfirm = {
                scope.launch {
                    orderToCancel ?.let {
                        val statusCode = cancelOrder(it.external_id)
                        when (statusCode.value) {
                            6 -> props.setAlert(errorAlert("Order was not found"))
                            10 -> props.setAlert(errorAlert("Order was cancelled but not refunded correctly. Please retry."))
                            200 -> props.setAlert(successAlert("Order was cancelled successfully !"))
                            else -> props.setAlert(errorAlert("Something went wrong"))
                        }
                        orderList = getUserOrderList(props.userId)
                        orderToCancel = null
                    }
                }
                popupTarget = null
            }
        }

    }
}

external interface ConfirmCancelPopupProps: Props {
    var popupTarget : HTMLButtonElement?
    var orderToCancel : Order?
    var onClickCancel : () -> Unit
    var onClickConfirm : () -> Unit
}

val ConfirmCancelPopup = FC<ConfirmCancelPopupProps> { props ->

    Popper {
        css {
            border = 2.px
            borderColor = Color(Const.ColorCode.BACKGROUND_GREY_DARKEST.code())
        }
        open = (props.popupTarget != null)
        anchorEl = props.popupTarget
        Box {
            css {
                border = 2.px
                borderColor = Color(Const.ColorCode.BACKGROUND_GREY_DARKEST.code())
                paddingTop = 1.vw
                paddingBottom = 1.vw
                paddingLeft = 2.vw
                paddingRight = 2.vw
                boxSizing = BoxSizing.borderBox
                width = 25.vw
                height = 15.vh
                display = Display.flex
                flexDirection = FlexDirection.column
                alignItems = AlignItems.center
                justifyContent = JustifyContent.spaceBetween
                backgroundColor = Color(Const.ColorCode.BACKGROUND_GREY_EVEN_DARKER.code())
            }
            +"Please confirm you wish to cancel your order ${props.orderToCancel?.label ?: ""} : "
            div {
                css {
                    display = Display.flex
                    flexDirection = FlexDirection.row
                    justifyContent = JustifyContent.spaceBetween
                    padding = 2.vw
                    boxSizing = BoxSizing.borderBox
                    width = 100.pct
                }
                Button {
                    +"Close"
                    onClick = {
                        props.onClickCancel()
                    }
                }

                Button {
                    +"Confirm"
                    onClick = {
                        props.onClickConfirm()
                    }
                }
            }
        }
    }
}