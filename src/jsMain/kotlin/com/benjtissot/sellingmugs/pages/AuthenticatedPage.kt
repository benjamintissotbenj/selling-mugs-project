package com.benjtissot.sellingmugs.pages

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.highLevel.FooterComponent
import com.benjtissot.sellingmugs.components.highLevel.NavigationBarComponent
import emotion.react.css
import io.ktor.client.call.*
import io.ktor.http.*
import io.ktor.util.logging.*
import kotlinx.coroutines.launch
import react.FC
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
            setAlert = props.setAlert
        }
    } else {
        div {
            css {
                mainPageDiv()
            }
            +"You are not authenticated"
        }
    }

    FooterComponent {}
}