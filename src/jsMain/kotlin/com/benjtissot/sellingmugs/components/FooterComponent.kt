package com.benjtissot.sellingmugs.components
import com.benjtissot.sellingmugs.HELLO_PATH
import com.benjtissot.sellingmugs.HOMEPAGE_PATH
import csstype.*
import react.*
import emotion.react.*
import mui.icons.material.Home
import mui.icons.material.Search
import mui.material.IconButton
import mui.material.IconButtonColor
import mui.material.Size
import react.dom.html.ReactHTML.a
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.footer
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.li
import react.dom.html.ReactHTML.nav
import react.dom.html.ReactHTML.ul
import react.router.useNavigate

external interface FooterProps : Props {
}

val FooterComponent = FC<FooterProps> { props ->
    val navigate = useNavigate()
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
