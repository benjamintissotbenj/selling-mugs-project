package com.benjtissot.sellingmugs.components
import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.entities.Session
import csstype.*
import emotion.react.css
import io.ktor.util.logging.*
import kotlinx.coroutines.launch
import mui.icons.material.*
import mui.material.IconButton
import mui.material.IconButtonColor
import mui.material.Size
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.nav
import react.router.NavigateFunction
import react.router.useNavigate

private val LOG = KtorSimpleLogger("NavigationBarComponent.kt")

external interface NavigationBarProps : SessionPageProps {
    var navigate: NavigateFunction
}

val NavigationBarComponent = FC<NavigationBarProps> { props ->
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
                            recordClick(props.session.clickDataId, Const.ClickType.search.toString())
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
                            recordClick(props.session.clickDataId, Const.ClickType.home.toString())
                        }
                        props.navigate.invoke(HOMEPAGE_PATH)
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
                        props.navigate.invoke(USER_INFO_PATH)
                    }
                }
            }

            // Login
            LoginButton {
                session = props.session
                updateSession = props.updateSession
            }
        }
    }
}

external interface LoginButtonProps : Props {
    var session: Session
    var updateSession: () -> Unit
}

val LoginButton = FC<LoginButtonProps> { props ->

    val navigate = useNavigate()
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
                    navigate.invoke(LOGIN_PATH)
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
                    navigate.invoke(LOGIN_PATH)
                }
            }

        }
    }
}
