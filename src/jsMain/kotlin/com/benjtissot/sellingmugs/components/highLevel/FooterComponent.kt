package com.benjtissot.sellingmugs.components.highLevel
import com.benjtissot.sellingmugs.Const
import csstype.*
import emotion.react.css
import react.FC
import react.Props
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.nav

external interface FooterProps : Props,
    react.dom.html.HTMLAttributes<org.w3c.dom.HTMLDivElement> {
}

val FooterComponent = FC<FooterProps> { props ->
    nav {
        css {
            display = Display.flex
            justifyContent = JustifyContent.spaceBetween
            alignItems = AlignItems.center
            height = 4.vh
            width = 100.pct
            position = Position.absolute
            bottom = 0.px
            left = 0.px
            backgroundColor = Color(Const.ColorCode.BACKGROUND_BLUE.code())
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
