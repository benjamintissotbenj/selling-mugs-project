package com.benjtissot.sellingmugs.components.highLevel
import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.buttons.LoginButton
import csstype.*
import emotion.react.css
import io.ktor.client.call.*
import io.ktor.http.*
import io.ktor.util.logging.*
import kotlinx.coroutines.launch
import mui.icons.material.Home
import mui.icons.material.Search
import mui.icons.material.ShoppingCart
import mui.material.*
import mui.material.Size
import react.FC
import react.create
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.nav
import react.useEffectOnce
import react.useState

private val LOG = KtorSimpleLogger("NavigationBarComponent.kt")

external interface NavigationBarProps : NavigationProps,
    react.dom.html.HTMLAttributes<org.w3c.dom.HTMLDivElement> {
}

val NavigationBarComponent = FC<NavigationBarProps> { props ->

    nav {
        css {
            justifySpaceBetween()
            minHeight = 40.px
            height = 8.vh
            backgroundColor = Color("#333")
            alignItems = AlignItems.center
        }

        h1 {
            css {
                color = NamedColor.white
                fontBig()
                marginLeft = 4.vw
                cursor = Cursor.pointer
            }
            +"Selling Mugs Project"
            onClick = {
                props.navigate(HOMEPAGE_PATH)
            }
        }

        // End Icons
        div {
            css {
                display = Display.flex
                margin = 0.px
                padding = 0.px
            }

            // Homepage
            div {
                css {
                    verticalAlign = VerticalAlign.middle
                    marginRight = 2.vw
                }
                IconButton {
                    size = Size.small
                    color = IconButtonColor.primary
                    Home()
                    onClick = {
                        scope.launch{
                            recordClick(props.session.clickDataId, Const.ClickType.HOME_NAV.toString())
                        }
                        frontEndRedirect = ""
                        props.navigate.invoke(HOMEPAGE_PATH)
                    }
                }
            }

            // Cart
            div {
                css {
                    verticalAlign = VerticalAlign.middle
                    marginRight = 2.vw
                }
                IconButton {
                    size = Size.small
                    color = IconButtonColor.primary
                    ShoppingCart()

                    onClick = {
                        scope.launch{
                            recordClick(props.session.clickDataId, Const.ClickType.CART_NAV.toString())
                        }
                        frontEndRedirect = ""
                        props.navigate.invoke(CART_PATH)
                    }
                }
            }

            // UserInfo
            /*div {
                css {
                    verticalAlign = VerticalAlign.middle
                    marginRight = 2.vw
                }
                IconButton {
                    size = Size.small
                    color = IconButtonColor.primary
                    PersonSearch()
                    onClick = {
                        scope.launch{
                            recordClick(props.session.clickDataId, Const.ClickType.USER_INFO_NAV.toString())
                        }
                        if ((props.session.user?.userType ?: Const.UserType.CLIENT) == Const.UserType.ADMIN){
                            props.navigate.invoke(ADMIN_PANEL_PATH)
                        } else {
                            props.navigate.invoke(USER_INFO_PATH)
                        }
                    }
                }
            }*/

            // Login
            LoginButton {
                session = props.session
                updateSession = props.updateSession
                navigate = props.navigate
            }
        }
    }
}

