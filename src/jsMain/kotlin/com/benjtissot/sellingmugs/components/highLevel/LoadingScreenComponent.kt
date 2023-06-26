package com.benjtissot.sellingmugs.components.highLevel

import com.benjtissot.sellingmugs.*
import csstype.Color
import csstype.px
import emotion.react.css
import mui.lab.LoadingButton
import mui.material.ButtonColor
import react.FC
import react.Props
import react.dom.html.ReactHTML.div

val LoadingScreenComponent = FC<Props> {
    div {
        css {
            contentCenteredVertically()
            contentCenteredHorizontally()
        }

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