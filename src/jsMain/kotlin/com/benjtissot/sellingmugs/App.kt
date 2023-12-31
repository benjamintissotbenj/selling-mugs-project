package com.benjtissot.sellingmugs

import com.benjtissot.sellingmugs.components.highLevel.AlertComponent
import com.benjtissot.sellingmugs.entities.local.Session
import com.benjtissot.sellingmugs.pages.*
import io.ktor.util.logging.*
import kotlinx.browser.document
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import react.*
import react.router.NavigateFunction
import react.router.Route
import react.router.Routes
import react.router.dom.BrowserRouter

private val LOG = KtorSimpleLogger("App.kt")

val scope = MainScope()
var frontEndRedirect = ""

val App = FC<Props> {
    var sessionApp: Session? by useState(null)
    val (alertState, setAlertState ) = useState(AlertState())

    fun setAlert(newAlertState: AlertState){
        setAlertState.invoke(newAlertState)
    }

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
                    element = BasicPage.create{
                        session = sessionApp!!
                        updateSession = updateSessionApp
                        setAlert = {alertState -> setAlert(alertState)}
                        internalPage = Homepage
                    }
                }
                Route {
                    path = CHECKOUT_PATH
                    element = AuthenticatedPage.create{
                        session = sessionApp!!
                        updateSession = updateSessionApp
                        setAlert = {alertState -> setAlert(alertState)}
                        internalPage = CheckoutPage
                        internalPagePath = CHECKOUT_PATH
                    }
                }
                Route {
                    path = USER_INFO_PATH
                    element = AuthenticatedPage.create{
                        session = sessionApp!!
                        updateSession = updateSessionApp
                        setAlert = {alertState -> setAlert(alertState)}
                        internalPage = UserInfoPage
                        internalPagePath = USER_INFO_PATH
                    }
                }
                Route {
                    path = ADMIN_PANEL_PATH
                    element = AuthenticatedPage.create{
                        session = sessionApp!!
                        updateSession = updateSessionApp
                        setAlert = {alertState -> setAlert(alertState)}
                        internalPage = AdminPanelPage
                        internalPagePath = ADMIN_PANEL_PATH
                    }
                }
                Route {
                    path = LOGIN_PATH
                    element = BasicPage.create{
                        session = sessionApp!!
                        updateSession = updateSessionApp
                        setAlert = {alertState -> setAlert(alertState)}
                        internalPage = LoginPage
                    }
                }
                Route {
                    path = REGISTER_PATH
                    element = BasicPage.create{
                        session = sessionApp!!
                        updateSession = updateSessionApp
                        setAlert = {alertState -> setAlert(alertState)}
                        internalPage = RegisterPage
                    }
                }
                Route {
                    path = CART_PATH
                    element = BasicPage.create{
                        session = sessionApp!!
                        updateSession = updateSessionApp
                        setAlert = {alertState -> setAlert(alertState)}
                        internalPage = CartPage
                    }
                }
                Route {
                    path = CUSTOM_MUG_PATH
                    element = BasicPage.create{
                        session = sessionApp!!
                        updateSession = updateSessionApp
                        setAlert = {alertState -> setAlert(alertState)}
                        internalPage = CustomMugPage
                    }
                }
                Route {
                    path = PROJECT_INFORMATION_PATH
                    element = BasicPage.create{
                        session = sessionApp!!
                        updateSession = updateSessionApp
                        setAlert = {alertState -> setAlert(alertState)}
                        internalPage = ProjectInformationPage
                    }
                }
                Route {
                    path = "$PRODUCT_INFO_PATH/:${Const.mugShortUrlHandle}"
                    element = BasicPage.create{
                        session = sessionApp!!
                        updateSession = updateSessionApp
                        setAlert = {alertState -> setAlert(alertState)}
                        internalPage = ProductInfoPage
                    }
                }
                Route {
                    path = GENERATION_RESULTS_PATH
                    element = BasicPage.create{
                        session = sessionApp!!
                        updateSession = updateSessionApp
                        setAlert = {alertState -> setAlert(alertState)}
                        internalPage = GenerationResultsPage
                    }
                }
            }
        }

        AlertComponent {
            this.alertState = alertState
            this.setAlert = {alert -> setAlert(alert)}
        }

    } ?: run {
        // Make the loader invisible when screen is loaded
        document.getElementById("loading-container")?.id = "invisible"
    }

}

external interface SessionPageProps: Props {
    var session: Session
    var updateSession: () -> Unit
    var setAlert: (AlertState) -> Unit
}

external interface NavigationProps: SessionPageProps {
    var navigate: NavigateFunction
}