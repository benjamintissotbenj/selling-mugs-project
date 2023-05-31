package com.benjtissot.sellingmugs

import com.benjtissot.sellingmugs.entities.Session
import com.benjtissot.sellingmugs.pages.*
import io.ktor.util.logging.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import mui.lab.LoadingButton
import react.*
import react.router.NavigateFunction
import react.router.Route
import react.router.Routes
import react.router.dom.BrowserRouter

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
        updateClientWithToken(sessionApp!!.jwtToken)

        val updateSessionApp: () -> Unit = {
            scope.launch {
                sessionApp = getSession()
            }
        }

        BrowserRouter {
            Routes {
                Route {
                    path = HOMEPAGE_PATH
                    element = Homepage.create{
                        session = sessionApp!!
                        updateSession = updateSessionApp
                    }
                }
                Route {
                    path = CHECKOUT_PATH
                    element = CheckoutPage.create{
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
                    path = ADMIN_PANEL_PATH
                    element = AdminPanelPage.create{
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
                Route {
                    path = REGISTER_PATH
                    element = RegisterPage.create{
                        session = sessionApp!!
                        updateSession = updateSessionApp
                    }
                }
                Route {
                    path = CART_PATH
                    element = CartPage.create{
                        session = sessionApp!!
                        updateSession = updateSessionApp
                    }
                }
            }
        }
    }
        ?: run {
            LoadingButton {

            }
        }
    // TODO : if no session, show loading screen

}

external interface SessionPageProps: Props {
    var session: Session
    var updateSession: () -> Unit
}

external interface NavigationProps: SessionPageProps {
    var navigate: NavigateFunction
}