package com.benjtissot.sellingmugs

import com.benjtissot.sellingmugs.components.*
import com.benjtissot.sellingmugs.entities.Artwork
import com.benjtissot.sellingmugs.entities.Mug
import com.benjtissot.sellingmugs.entities.Session
import com.benjtissot.sellingmugs.entities.User
import io.ktor.util.logging.*
import react.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
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
                element = createElement(type = HomepageComponent)
            }
            Route {
                path = HELLO_PATH
                element = createElement(helloComponent)
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