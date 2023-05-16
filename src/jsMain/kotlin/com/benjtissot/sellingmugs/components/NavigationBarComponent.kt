package com.benjtissot.sellingmugs.components
import com.benjtissot.sellingmugs.*
import csstype.*
import emotion.react.css
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

private val LOG = KtorSimpleLogger("NavigationBarComponent.kt")

external interface NavigationBarProps : NavigationProps {
}

val NavigationBarComponent = FC<NavigationBarProps> { props ->
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
                            recordClick(props.session.clickDataId, Const.ClickType.SEARCH.toString())
                        }
                        props.navigate.invoke(HELLO_PATH)
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
                            recordClick(props.session.clickDataId, Const.ClickType.HOME.toString())
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
                            recordClick(props.session.clickDataId, Const.ClickType.CART.toString())
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
                            recordClick(props.session.clickDataId, Const.ClickType.USER_INFO.toString())
                        }
                        props.navigate.invoke(USER_INFO_PATH)
                    }
                }
            }

            // Login
            LoginButton {
                session = props.session
                updateSession = props.updateSession
                navigate = props.navigate
            }
        }
    }
}

external interface LoginButtonProps : NavigationProps {
}

val LoginButton = FC<LoginButtonProps> { props ->

    div {
        css {
            verticalAlign = VerticalAlign.middle
            marginRight = 2.vw
        }
        IconButton {
            size = Size.small
            color = IconButtonColor.primary
            if (props.session.user == null || props.session.jwtToken.isBlank()){
                Person()
                onClick = {
                    props.navigate.invoke(LOGIN_PATH)
                }
            } else {
                div {
                    css {
                        marginRight = 1.vw
                    }
                    +props.session.user!!.getNameInitial()
                }
                PersonOutline()
                onClick = {
                    props.navigate.invoke(LOGIN_PATH)
                }
            }

        }
    }
}
