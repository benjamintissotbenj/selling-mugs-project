package com.benjtissot.sellingmugs.components.popups

import com.benjtissot.sellingmugs.Const
import com.benjtissot.sellingmugs.components.lists.mugItemHeight
import com.benjtissot.sellingmugs.entities.printify.order.Order
import com.benjtissot.sellingmugs.popupBoxDefault
import csstype.*
import emotion.react.css
import mui.material.Box
import mui.material.Button
import mui.material.Popper
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLDivElement
import react.FC
import react.Props
import react.dom.html.ReactHTML
import react.dom.html.ReactHTML.div

external interface ConfirmCancelPopupProps: Props {
    var onClickCancel : () -> Unit
    var onClickConfirm : () -> Unit
}

val ConfirmCancelButtons = FC<ConfirmCancelPopupProps> { props ->
    div {
        css {
            display = Display.flex
            flexDirection = FlexDirection.row
            justifyContent = JustifyContent.spaceBetween
            padding = 1.vh
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

external interface ConfirmCheckoutPopupProps: ConfirmCancelPopupProps {
    var popupTarget : HTMLButtonElement?
    var amountOfMugs: Int
}

val ConfirmCheckoutPopup = FC<ConfirmCheckoutPopupProps> { props ->

    Popper {
        css {
            border = 2.px
            borderColor = Color(Const.ColorCode.BACKGROUND_GREY_DARKEST.code())
        }
        open = (props.popupTarget != null)
        anchorEl = props.popupTarget
        Box {
            css {
                popupBoxDefault()
            }
            +"Please confirm you wish to pay for ${props.amountOfMugs} mugs. "
            ConfirmCancelButtons {
                this.onClickConfirm = props.onClickConfirm
                this.onClickCancel = props.onClickCancel
            }
        }
    }
}

external interface ConfirmOrderCancelPopupProps: ConfirmCancelPopupProps {
    var popupTarget : HTMLButtonElement?
    var orderToCancel : Order?
}

val ConfirmOrderCancelPopup = FC<ConfirmOrderCancelPopupProps> { props ->

    Popper {
        css {
            border = 2.px
            borderColor = Color(Const.ColorCode.BACKGROUND_GREY_DARKEST.code())
        }
        open = (props.popupTarget != null)
        anchorEl = props.popupTarget
        Box {
            css {
                popupBoxDefault()
            }
            +"Please confirm you wish to cancel your order ${props.orderToCancel?.label ?: ""} : "

            ConfirmCancelButtons {
                this.onClickConfirm = props.onClickConfirm
                this.onClickCancel = props.onClickCancel
            }
        }
    }
}


external interface MugDetailsPopupProps: Props {
    var popupTarget : HTMLDivElement?
}

val MugDetailsPopup = FC<MugDetailsPopupProps> { props ->
    Popper {
        css {
            border = 2.px
            borderColor = Color(Const.ColorCode.BACKGROUND_GREY_DARKEST.code())
        }
        open = (props.popupTarget != null)
        anchorEl = props.popupTarget
        Box {
            css {
                popupBoxDefault()
                marginTop = - mugItemHeight
                width = 30.vw
                height = 30.vw
            }
            +"This is a popup for mug details"

        }
    }
}