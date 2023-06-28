package com.benjtissot.sellingmugs.components.highLevel

import com.benjtissot.sellingmugs.*
import csstype.Color
import csstype.NamedColor
import csstype.px
import csstype.vh
import emotion.react.css
import mui.lab.LoadingButton
import mui.material.Backdrop
import mui.material.ButtonColor
import mui.system.sx
import react.FC
import react.Props
import react.dom.html.ReactHTML.div


external interface LoadingProps: Props {
    var open: Boolean
    var onClickClose : () -> Unit
}

val LoadingComponent = FC<LoadingProps> { props ->

    Backdrop {
        sx {"{ color: '#ffffff', zIndex: (theme) => theme.zIndex.drawer + 100000 }"}
        open = props.open

        div {
            css {
                contentCenteredVertically()
                contentCenteredHorizontally()
                fontBig()
                boxNormalSmall()
                boxBlueShade()
                color = Color(Const.ColorCode.BLUE.code())
                backgroundColor = NamedColor.white
                padding = 2.vh
            }
            PopupHeaderComponent {
                title = "Please Wait"
                onClickClose = props.onClickClose
            }

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
        }
    }
}