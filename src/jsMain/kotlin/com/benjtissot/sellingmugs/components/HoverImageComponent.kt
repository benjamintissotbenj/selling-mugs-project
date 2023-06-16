package com.benjtissot.sellingmugs.components

import csstype.*
import emotion.react.css
import mui.material.Button
import react.FC
import react.Props
import react.dom.html.ReactHTML


external interface HoverImageProps: Props {
    var width: Width?
    var height: Height?
    var srcMain: String
    var srcHover: String
    var onClick: () -> Unit
}

val HoverImageComponent = FC<HoverImageProps> { props ->
    Button {
        css {
            position = Position.relative
            height = props.height
            width = props.width
            marginTop = 4.vw
        }
        onClick = {
            props.onClick()
        }
        ReactHTML.img {
            css {
                position = Position.absolute
                height = props.height
                width = props.width
                right = 0.px
                top = 0.px
                left = 0.px
                bottom = 0.px
            }
            src = props.srcMain
        }
        ReactHTML.img {
            css {
                position = Position.absolute
                height = props.height
                width = props.width
                right = 0.px
                top = 0.px
                left = 0.px
                bottom = 0.px
                objectFit = ObjectFit.contain
                opacity = "0".unsafeCast<Opacity>()
                transition = "opacity .2s".unsafeCast<Transition>()
                hover {
                    opacity = "1".unsafeCast<Opacity>()
                }
            }
            src = props.srcHover
        }
    }
}