package com.benjtissot.sellingmugs.pages

import com.benjtissot.sellingmugs.*
import emotion.react.css
import io.ktor.client.call.*
import io.ktor.http.*
import io.ktor.util.logging.*
import kotlinx.coroutines.launch
import mui.lab.LoadingButton
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.router.useNavigate
import react.useEffectOnce
import react.useState

private val LOG = KtorSimpleLogger("AuthenticatedPage.kt")

external interface AuthenticatedPageProps : SessionPageProps {
    var internalPage : FC<NavigationProps>
    var internalPagePath: String?
}
val AuthenticatedPage = FC<AuthenticatedPageProps> { props ->

    val navigateAuthenticated = useNavigate()
    var loggedIn : Boolean? by useState(null)

    useEffectOnce {
        scope.launch {
            val loggedInResponse = isLoggedIn()
            val loggedInValue = loggedInResponse.status != HttpStatusCode.Unauthorized && (loggedInResponse.body<String>() == "true")

            if (!loggedInValue) {
                frontEndRedirect = props.internalPagePath ?: HOMEPAGE_PATH
                navigateAuthenticated.invoke(LOGIN_PATH)
            }
            loggedIn = loggedInValue
        }
    }

    BasicPage {
        session = props.session
        updateSession = props.updateSession
        setAlert = props.setAlert

        this.internalPage =
            when (loggedIn){
                true -> props.internalPage
                false -> FC<Props> {
                    div {
                        +"You are not authenticated"
                    }
                }
                null -> FC<Props> {
                    div {
                        css {
                            contentCenteredHorizontally()
                            contentCenteredVertically()
                        }
                        LoadingButton {

                        }
                    }
                }
            }

    }

}