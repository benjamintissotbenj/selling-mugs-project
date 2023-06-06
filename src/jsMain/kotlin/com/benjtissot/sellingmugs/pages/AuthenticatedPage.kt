package com.benjtissot.sellingmugs.pages

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.FooterComponent
import com.benjtissot.sellingmugs.components.NavigationBarComponent
import io.ktor.client.call.*
import io.ktor.http.*
import io.ktor.util.logging.*
import kotlinx.coroutines.launch
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.router.useNavigate
import react.useEffectOnce
import react.useState

private val LOG = KtorSimpleLogger("AuthenticatedPage.kt")

external interface AuthenticatedPageProps : SessionPageProps {
    var internalPage : FC<SessionPageProps>
}
val AuthenticatedPage = FC<AuthenticatedPageProps> { props ->

    val navigateAuthenticated = useNavigate()
    var loggedIn by useState(false)

    useEffectOnce {
        scope.launch {
            val loggedInResponse = isLoggedIn()
            val loggedInValue = loggedInResponse.status != HttpStatusCode.Unauthorized && (loggedInResponse.body<String>() == "true")

            LOG.debug("User is logged in with response: ${loggedInResponse.status != HttpStatusCode.Unauthorized} && ${loggedInResponse.body<Boolean>()} : $loggedInValue")
            if (!loggedInValue) {
                navigateAuthenticated.invoke(LOGIN_PATH)
            }
            loggedIn = loggedInValue
        }
    }

    NavigationBarComponent {
        session = props.session
        updateSession = props.updateSession
        navigate = navigateAuthenticated
    }

    if (loggedIn){
        props.internalPage {
            session = props.session
            updateSession = props.updateSession
        }
    } else {
        div {
            divDefaultCss()
            +"You are not authenticated"
        }
    }

    FooterComponent {}
}