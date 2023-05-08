package com.benjtissot.sellingmugs

import com.benjtissot.sellingmugs.components.*
import com.benjtissot.sellingmugs.entities.Artwork
import com.benjtissot.sellingmugs.entities.Mug
import io.ktor.util.logging.*
import react.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import react.dom.html.ReactHTML.div
import react.router.Route
import react.router.Routes
import react.router.dom.BrowserRouter


private val scope = MainScope()



val App = FC<Props> {
    val LOG = KtorSimpleLogger("App.kt")
    var mugList by useState(emptyList<Mug>())


    BrowserRouter {
        Routes {
            Route {
                path = HOMEPAGE_PATH
                element = createElement(HomepageComponent)

            }
            Route {
                path = HELLO_PATH
                element = createElement(helloComponent)
            }
        }
    }

}

val helloComponent = FC<Props> {
    div {
        +"Hello Component"
    }
}