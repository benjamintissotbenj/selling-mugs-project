package com.benjtissot.sellingmugs.components

import com.benjtissot.sellingmugs.MugCartItem
import com.benjtissot.sellingmugs.divDefaultCss
import com.benjtissot.sellingmugs.divDefaultHorizontalCss
import com.benjtissot.sellingmugs.entities.Mug
import com.benjtissot.sellingmugs.justifySpaceBetween
import csstype.*
import emotion.react.css
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.img


external interface MugCartItemProps: Props {
    var mugCartItem: MugCartItem
}

val MugCartItemComponent = FC<MugCartItemProps> {
        props ->
    div {
        css {
            divDefaultHorizontalCss()
            justifySpaceBetween()
            width = 30.vw
            maxWidth = 30.rem
            height = 2.vh
            padding = 1.rem
        }

        div {
            css {
                fontSize = 2.vh
            }
            +props.mugCartItem.mug.name
        }

        div {
            css {
                alignSelf = AlignSelf.flexEnd
                fontSize = 2.vh
            }
            +"x${props.mugCartItem.amount}"
        }
    }
}