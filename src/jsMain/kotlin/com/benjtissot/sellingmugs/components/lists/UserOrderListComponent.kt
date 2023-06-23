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

    var orderList : List<Order>? by useState(null)

    useEffectOnce {
        scope.launch {
            orderList = getUserOrderList(props.userId)
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
        header {
            css {
                width = 100.pct
                height = 4.pct
            }
            div {
                css {
                    fontBig()
                    marginLeft = 10.vw
                }
                +"Your orders"
            }

            // TODO : put a button to filter according to a given period of time
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
                height = 95.pct
            }
            orderList!!.forEach { order ->
                UserOrderItemComponent {
                    this.order = order
                    onClickCancel = { order ->
                        props.setAlert(errorAlert("Cancelling order of label ${order.label}"))
                    }
                }
            }
        }
    }
}