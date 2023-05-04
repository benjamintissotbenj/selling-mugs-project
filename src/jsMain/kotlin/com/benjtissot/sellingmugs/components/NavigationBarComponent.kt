package com.benjtissot.sellingmugs.components
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
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.li
import react.dom.html.ReactHTML.nav
import react.dom.html.ReactHTML.ul

external interface NavProps : Props {
    var links: List<Pair<String, String>> // text and url pairs
}

val NavigationBarComponent = FC<NavProps> { props ->
    nav {
        css {
            backgroundColor = Color("#333")
            display = Display.flex
            justifyContent = JustifyContent.spaceBetween
            alignItems = AlignItems.center
            height = 8.vh
            borderRadius = 4.vw
        }

        h1 {
            css {
                color = NamedColor.white
                fontSize = 3.vh
                marginLeft = 4.vw
            }
            +"Selling Mugs Project"
        }
        div {
            css {
                display = Display.flex
                margin = 0.px
                padding = 0.px
            }
            for (i in 1..5) {
                div {
                    css {
                        display = Display.flex
                        textAlign = TextAlign.center
                        color = NamedColor.white
                        marginRight = 1.vw
                        marginLeft = 1.vw
                        fontSize = 2.vh
                        maxHeight = 3.vh
                    }
                    +"Category $i"

                }
            }

            div {
                css {
                    verticalAlign = VerticalAlign.middle
                    marginRight = 2.vw
                }
                IconButton {
                    size = Size.small
                    color = IconButtonColor.primary
                    Search()
                    onClick = {  }
                }
            }

            div {
                css {
                    verticalAlign = VerticalAlign.middle
                    marginRight = 2.vw
                }
                IconButton {
                    size = Size.small
                    color = IconButtonColor.primary
                    Home()
                    onClick = {  }
                }
            }
        }
    }
}
