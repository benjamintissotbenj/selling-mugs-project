package com.benjtissot.sellingmugs.components
import csstype.*
import emotion.react.css
import react.FC
import react.Props
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.nav
import react.router.useNavigate

external interface FooterProps : Props {
}

val FooterComponent = FC<FooterProps> { props ->
    nav {
        css {
            display = Display.flex
            justifyContent = JustifyContent.spaceBetween
            alignItems = AlignItems.center
            height = 8.vh
            borderRadius = 4.vw
            position = Position.absolute
            bottom = 0.px
        }

        h1 {
            css {
                color = NamedColor.black
                fontSize = 3.vh
                marginLeft = 4.vw
            }
            +"Footer"
        }

    }
}
