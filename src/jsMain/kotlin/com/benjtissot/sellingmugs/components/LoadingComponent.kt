package com.benjtissot.sellingmugs.components

import com.benjtissot.sellingmugs.*
import csstype.Color
import csstype.px
import emotion.react.css
import mui.lab.LoadingButton
import mui.material.Backdrop
import mui.material.ButtonColor
import mui.material.CircularProgress
import mui.system.sx
import react.FC
import react.Props
import react.dom.html.ReactHTML.div


external interface LoadingProps: Props {
    var open: Boolean
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
            }
            +"Please Wait"
            LoadingButton {
                css {
                    margin = 16.px
                    width = 50.px
                    height = 50.px
                    color = Color(Const.ColorCode.BLUE.code())
                }
                color = ButtonColor.inherit
                loading = true
            }
        }
    }
}