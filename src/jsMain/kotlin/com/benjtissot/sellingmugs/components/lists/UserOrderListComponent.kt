package com.benjtissot.sellingmugs.components.lists

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.entities.printify.order.Order
import csstype.*
import emotion.react.css
import kotlinx.coroutines.launch
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

    var orderList : List<Order> by useState(emptyList())

    useEffectOnce {
        scope.launch {
            orderList = getUserOrderList(props.userId)
        }
    }

    header {
        css {
            width = 100.pct
        }
        div {
            css {
                fontBig()
                marginLeft = 10.vw
            }
            +"Your orders"
        }
    }
    div {
        css {
            display = Display.flex
            flexDirection = FlexDirection.column
            overflowY = Overflow.scroll
            scrollBehavior = ScrollBehavior.smooth
            paddingBlock = 1.rem
            width = 80.vw
            maxWidth = 80.rem
            maxHeight = 70.vh
        }
        orderList.forEach { order ->
            UserOrderItemComponent {
                this.order = order
                onClickCancel = { mugCartItem ->
                    props.setAlert(errorAlert("Cancelling order of label ${order.label}"))
                }
            }
        }

    }


}