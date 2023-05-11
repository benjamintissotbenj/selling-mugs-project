package com.benjtissot.sellingmugs

import com.benjtissot.sellingmugs.components.NavigationBarComponent
import com.benjtissot.sellingmugs.entities.Session
import io.ktor.util.logging.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import react.*
import react.dom.html.ReactHTML.div
import react.router.Route
import react.router.Routes
import react.router.dom.BrowserRouter
import react.router.useNavigate

private val LOG = KtorSimpleLogger("App.kt")

val scope = MainScope()

val App = FC<Props> {
    var sessionApp: Session? by useState(null)

    // Calling for the session
    useEffectOnce {
        scope.launch {
            sessionApp = getSession()
        }
    }

    sessionApp?.let{
        val updateSessionApp: () -> Unit = {
            scope.launch {
                sessionApp = getSession()
            }
        }

        BrowserRouter {
            Routes {
                Route {
                    path = "/"
                    element = Homepage.create{
                        session = sessionApp!!
                        updateSession = updateSessionApp
                    }
                }
                Route {
                    path = HOMEPAGE_PATH
                    element = Homepage.create{
                        session = sessionApp!!
                        updateSession = updateSessionApp
                    }
                }
                Route {
                    path = HELLO_PATH
                    element = helloComponent.create{
                        session = sessionApp!!
                        updateSession = updateSessionApp
                    }
                }
                Route {
                    path = USER_INFO_PATH
                    element = UserInfoPage.create{
                        session = sessionApp!!
                        updateSession = updateSessionApp
                    }
                }
                Route {
                    path = LOGIN_PATH
                    element = LoginPage.create{
                        session = sessionApp!!
                        updateSession = updateSessionApp
                    }
                }
            }
        }
    }
    // TODO : if no session, show loading screen

}

external interface SessionPageProps: Props {
    var session: Session
    var updateSession: () -> Unit
}

val helloComponent = FC<SessionPageProps> {props ->
    val navigateHello = useNavigate()
    NavigationBarComponent {
        session = props.session
        updateSession = props.updateSession
        navigate = navigateHello
    }
    div {
        +"Hello Component"
    }

}