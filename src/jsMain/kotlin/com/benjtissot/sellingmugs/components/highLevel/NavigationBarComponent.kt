package com.benjtissot.sellingmugs.components.highLevel
import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.LoginButton
import csstype.*
import emotion.react.css
import io.ktor.client.call.*
import io.ktor.http.*
import io.ktor.util.logging.*
import kotlinx.coroutines.launch
import mui.icons.material.*
import mui.material.IconButton
import mui.material.IconButtonColor
import mui.material.Size
import react.FC
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.nav
import react.useEffectOnce
import react.useState

private val LOG = KtorSimpleLogger("NavigationBarComponent.kt")

external interface NavigationBarProps : NavigationProps {
}

val NavigationBarComponent = FC<NavigationBarProps> { props ->

    var loggedIn by useState(false)

    useEffectOnce {
        scope.launch {
            val loggedInResponse = isLoggedIn()
            loggedIn = (loggedInResponse.status != HttpStatusCode.Unauthorized) && (loggedInResponse.body<String>() == "true")
        }
    }

    nav {
        css {
            justifySpaceBetween()
            backgroundColor = Color("#333")
            alignItems = AlignItems.center
            height = 8.vh
        }

        h1 {
            css {
                color = NamedColor.white
                fontSize = 3.vh
                marginLeft = 4.vw
            }
            +"Selling Mugs Project"
        }

        // End Icons
        div {
            css {
                display = Display.flex
                margin = 0.px
                padding = 0.px
            }


            // Search
            div {
                css {
                    verticalAlign = VerticalAlign.middle
                    marginRight = 2.vw
                }
                IconButton {
                    size = Size.small
                    color = IconButtonColor.primary
                    Search()
                    onClick = {
                        scope.launch{
                            recordClick(props.session.clickDataId, Const.ClickType.CHECKOUT_NAV.toString())
                        }
                        props.navigate.invoke(CHECKOUT_PATH)
                    }
                }
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
                        props.navigate.invoke(CART_PATH)
                    }
                }
            }

            // UserInfo
            div {
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
            }

            // Login
            LoginButton {
                session = props.session
                updateSession = props.updateSession
                navigate = props.navigate
                this.loggedIn = loggedIn
            }
        }
    }
}

