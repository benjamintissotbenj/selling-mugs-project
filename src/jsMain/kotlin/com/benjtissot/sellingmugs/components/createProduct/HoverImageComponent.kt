package com.benjtissot.sellingmugs.components.createProduct

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
            props.width?.let { width = it }
            props.height?.let { height = it }
            margin = 3.pct
            boxSizing = BoxSizing.borderBox
        }
        onClick = {
            props.onClick()
        }
        ReactHTML.img {
            css {
                position = Position.absolute
                width = 100.pct
                right = 0.px
                top = 0.px
                left = 0.px
                bottom = 0.px
                objectFit = ObjectFit.contain
            }
            src = props.srcMain
        }
        ReactHTML.img {
            css {
                position = Position.absolute
                width = 100.pct
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