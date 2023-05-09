package com.benjtissot.sellingmugs

import com.benjtissot.sellingmugs.Pages.Homepage
import com.benjtissot.sellingmugs.Pages.UserInfoPage
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


val App = FC<Props> {
    val LOG = KtorSimpleLogger("App.kt")
    var sessionApp: Session? by useState(null)


    BrowserRouter {
        Routes {
            Route {
                path = HOMEPAGE_PATH
                element = createElement(type = Homepage)
            }
            Route {
                path = HELLO_PATH
                element = createElement(helloComponent)
            }
            Route {
                path = USER_INFO_PATH
                element = createElement(type = UserInfoPage)
            }
        }
    }

}

val helloComponent = FC<Props> {

    var session: Session? by useState(null)
    val scope = MainScope()

    // At first initialisation, get the list
    // Alternative is useState when we want to persist something across re-renders
    useEffectOnce {
        scope.launch {
            session = getSession()
        }
    }
    session?.also{
        NavigationBarComponent {
            currentSession = session!!
            updateSession = {
                scope.launch {
                    session = getSession()
                }
            }
        }
        div {
            +"Hello Component"
        }
    }
}