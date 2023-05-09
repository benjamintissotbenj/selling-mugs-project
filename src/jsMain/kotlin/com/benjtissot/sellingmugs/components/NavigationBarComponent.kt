package com.benjtissot.sellingmugs.components
import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.entities.Session
import com.benjtissot.sellingmugs.entities.User
import csstype.*
import react.*
import emotion.react.*
import io.ktor.util.logging.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import mui.icons.material.*
import mui.material.IconButton
import mui.material.IconButtonColor
import mui.material.Size
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.nav
import react.router.useNavigate

val LOG = KtorSimpleLogger("NavigationBarComponent.kt")

private val scope = MainScope()

external interface NavProps : Props {
    var currentSession: Session
}

val NavigationBarComponent = FC<NavProps> { props ->
    val navigate = useNavigate()
    LOG.error("${props.currentSession}")
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
                            recordClick(props.currentSession.clickDataId, Const.ClickType.search.toString())
                        }
                        navigate.invoke(HELLO_PATH)
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
                            recordClick(props.currentSession.clickDataId, Const.ClickType.home.toString())
                        }
                        navigate.invoke(HOMEPAGE_PATH)
                    }
                }
            }

            // Login
            LoginButton {user = props.currentSession.user}
        }
    }
}

external interface LoginButtonProps : Props {
    var user: User?
}

val LoginButton = FC<LoginButtonProps> { props ->

    val navigate = useNavigate()
    LOG.error("${props.user}")
    div {
        css {
            verticalAlign = VerticalAlign.middle
            marginRight = 2.vw
        }
        props.user?.also {
            // If User is non null
            IconButton {
                div {
                    css {
                        marginRight = 1.vw
                    }
                    +props.user!!.getNameInitial()
                }
                size = Size.small
                color = IconButtonColor.primary
                PersonOutline()
                onClick = {
                    navigate.invoke(LOGIN_PATH)
                }
            }
        } ?: run {
            // If user is null
            LOG.debug("User is null")
            div {
                IconButton {
                    size = Size.small
                    color = IconButtonColor.primary
                    Person()
                    onClick = {
                        val user = User("123","Benjamin", "Tissot", "123", "123", Const.UserType.ADMIN, "23")
                        MainScope().launch {
                            setUser(user)
                        }
                        navigate.invoke(LOGIN_PATH)
                    }
                }
            }
        }
    }
}
