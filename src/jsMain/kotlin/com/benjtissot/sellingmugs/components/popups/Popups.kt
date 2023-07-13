package com.benjtissot.sellingmugs.components.popups

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.createProduct.SweepImageComponent
import com.benjtissot.sellingmugs.entities.Mug
import com.benjtissot.sellingmugs.entities.printify.order.Order
import csstype.*
import emotion.react.css
import kotlinx.coroutines.launch
import mui.icons.material.AddShoppingCart
import mui.lab.LoadingButton
import mui.material.*
import org.w3c.dom.Element
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.events.Event
import react.FC
import react.Props
import react.dom.html.ReactHTML
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.img
import react.useEffect
import react.useRef

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
    var onMouseLeavePopup : () -> Unit
    var mug: Mug?
    var marginTop : Number
    var marginBottom : Number
    var onClickAddToCart: (Mug?) -> Unit
}

val MugDetailsPopup = FC<MugDetailsPopupProps> { props ->

    Popper {
        css {
            border = 2.px
            borderColor = Color(Const.ColorCode.BACKGROUND_GREY_DARKEST.code())
        }
        open = (props.popupTarget != null)
        anchorEl = props.popupTarget
        if (props.mug == null){
            LoadingButton {
                css {
                    margin = 16.px
                    width = 100.px
                    height = 100.px
                    color = Color(Const.ColorCode.BLUE.code())
                }
                color = ButtonColor.inherit
                loading = true
            }
        } else {
            val mug = props.mug!!
            Box {
                css {
                    popupBoxDefault()
                    boxShade()
                    overflow = Overflow.hidden
                    backgroundColor = NamedColor.white
                    marginTop = props.marginTop.rem
                    marginBottom = props.marginBottom.rem
                    width = 40.vh
                    height = 50.vh
                }
                onMouseLeave = {
                    props.onMouseLeavePopup()
                }

                SweepImageComponent {
                    height = 10.rem
                    width = 10.rem
                    srcList = mug.getAllPictureSrcs()
                    refresh = false
                }
                div {
                    +mug.name
                }
                div {
                    css {
                        fontSmall()
                    }
                    +mug.description
                }
                div {
                    +"£${mug.price}"
                }
                IconButton {
                    AddShoppingCart()
                    div {
                        css {
                            fontNormal()
                            margin = 2.vw
                        }
                        +"Add to cart"
                    }
                    onClick = {
                        props.onClickAddToCart(props.mug!!)
                    }
                }
            }
        }
    }
}
