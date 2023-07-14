package com.benjtissot.sellingmugs.components.highLevel
import com.benjtissot.sellingmugs.*
import csstype.*
import emotion.react.css
import kotlinx.browser.window
import mui.icons.material.GitHub
import mui.icons.material.LinkedIn
import mui.material.Icon
import mui.material.IconSize
import react.FC
import react.Props
import react.dom.html.ReactHTML.a
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.nav
import react.router.NavigateFunction

external interface FooterProps : Props,
    react.dom.html.HTMLAttributes<org.w3c.dom.HTMLDivElement> {
    var navigate: NavigateFunction
}

val FooterComponent = FC<FooterProps> { props ->
    div {
        css {
            display = Display.flex
            flexDirection = FlexDirection.row
            justifyContent = JustifyContent.spaceBetween
            alignItems = AlignItems.center
            height = 5.vh
            width = 100.pct
            position = Position.absolute
            zIndex = "0".unsafeCast<ZIndex>()
            bottom = 0.px
            left = 0.px
            backgroundColor = Color(Const.ColorCode.BACKGROUND_GREY.code())
        }
        div {
            css {
                boxSizing = BoxSizing.borderBox
                width = 25.vw
                marginLeft = 5.vw
                display = Display.flex
                flexDirection = FlexDirection.row
            }
        }

        div {
            css {
                fontSmall()
                color = NamedColor.black
                fontSize = 3.vh
                marginLeft = 1.vw
                height = 4.vh
                display = Display.flex
                flexDirection = FlexDirection.row
                justifyContent = JustifyContent.center
                alignItems = AlignItems.center
            }
            div {
                css {
                    fontNormal()
                }
                +"Contact us"
            }
            Icon {
                css {
                    marginInline = 1.vw
                    cursor = Cursor.pointer
                }
                fontSize = IconSize.medium
                LinkedIn()
                onClick = {
                    window.open(Const.contactLinkedin, "_blank")
                }
            }
            Icon {
                css {
                    cursor = Cursor.pointer
                }
                fontSize = IconSize.medium
                GitHub()
                onClick = {
                    window.open(Const.contactGitHub, "_blank")
                }
            }

        }

        div {
            css {
                fontSmall()
                width = 25.vw
                boxSizing = BoxSizing.borderBox
                display = Display.flex
                flexDirection = FlexDirection.rowReverse
                marginRight = 5.vw
                textDecoration = TextDecoration.underline
                cursor = Cursor.pointer
            }
            +"Project information"
            onClick = {
                props.navigate.invoke(PROJECT_INFORMATION_PATH)
            }
        }
    }
}
