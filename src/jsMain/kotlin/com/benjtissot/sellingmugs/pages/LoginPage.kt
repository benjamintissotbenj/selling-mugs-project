package com.benjtissot.sellingmugs.pages

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.forms.LoginFormComponent
import com.benjtissot.sellingmugs.components.highLevel.FooterComponent
import com.benjtissot.sellingmugs.components.highLevel.NavigationBarComponent
import csstype.*
import emotion.react.css
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.logging.*
import kotlinx.coroutines.launch
import org.komputing.khash.sha256.extensions.sha256
import react.FC
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.router.NavigateFunction
import react.router.useNavigate

private val LOG = KtorSimpleLogger("loginPage.kt")

external interface LoginPageProps : SessionPageProps {
}

val LoginPage = FC<LoginPageProps> { props ->
    val navigateLogin = useNavigate()

    NavigationBarComponent {
        session = props.session
        updateSession = props.updateSession
        navigate = navigateLogin
    }

    // General page div
    div {
        css {
            mainPageDiv()
            alignSelf = AlignSelf.center
            display = Display.flex
            flexDirection = FlexDirection.column
            alignItems = AlignItems.center
        }

        // Creating login form
        LoginFormComponent {
            onSubmit = { email, clearPassword ->
                val hashedPassword = clearPassword.sha256().toString()
                scope.launch {
                    val httpResponse = login(email, hashedPassword)
                    props.updateSession()
                    onLoginResponse(httpResponse, navigateLogin)
                }
            }
        }

        // Register invitation
        div {
            css {
                display = Display.flex
                flexDirection = FlexDirection.row
                alignItems = AlignItems.center
            }

            div {
                divDefaultCss()
                +"Not a customer yet? "
            }
            button {
                css {
                    marginLeft = 2.vw
                    borderRadius = 2.vh
                    backgroundColor = NamedColor.transparent
                    fontSize = 2.vh
                }
                +"Register now"
                onClick = {
                    LOG.debug("Click on Register")
                    navigateLogin.invoke(REGISTER_PATH)
                }
            }
        }

    }

    FooterComponent{}
}

suspend fun onLoginResponse(httpResponse: HttpResponse, navigateFunction: NavigateFunction){
    when (httpResponse.status) {
        HttpStatusCode.OK -> {
            // Using local variable because otherwise update is not atomic
            val tokenString = httpResponse.body<String>()
            updateClientWithToken(tokenString)
            navigateFunction.invoke(HOMEPAGE_PATH)
        }
        HttpStatusCode.Conflict -> {
            // User exists but is not authenticated
            LOG.error("Credentials not valid for login")
        }
        HttpStatusCode.BadGateway -> {
            // Session not found or couldn't be updated
            LOG.error("Session not found or couldn't be updated")
        }
        HttpStatusCode.InternalServerError -> {
            // Any other error
            LOG.error("Unexpected server error")
        }
        else -> {
            LOG.error("Unknown error")
        }
    }
}