package com.benjtissot.sellingmugs.pages

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.forms.LoginFormComponent
import com.benjtissot.sellingmugs.entities.local.LoginInfo
import csstype.*
import emotion.react.css
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.logging.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.komputing.khash.sha256.extensions.sha256
import react.FC
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.router.NavigateFunction

private val LOG = KtorSimpleLogger("loginPage.kt")


val LoginPage = FC<NavigationProps> { props ->

    div {
        css {
            contentCenteredHorizontally()
        }
        // Creating login form
        LoginFormComponent {
            onSubmit = { email, clearPassword ->
                val hashedPassword = clearPassword.sha256().toString()
                scope.launch {
                    val loginInfo = LoginInfo(email, hashedPassword)
                    val httpResponse = login(loginInfo)
                    recordClick(props.session.clickDataId, Const.ClickType.LOGIN.type)
                    onLoginResponse(httpResponse, props.navigate)
                    props.updateSession()
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
                    props.navigate.invoke(REGISTER_PATH)
                }
            }
        }
    }
}

suspend fun onLoginResponse(httpResponse: HttpResponse, navigateFunction: NavigateFunction){
    LOG.debug("onLoginResponse: $httpResponse")
    when (httpResponse.status) {
        HttpStatusCode.OK -> {
            // Using local variable because otherwise update is not atomic
            val tokenString = httpResponse.body<String>()
            updateClientWithToken(tokenString)
            if (frontEndRedirect.isBlank()){
                navigateFunction.invoke(HOMEPAGE_PATH)
            } else {
                val redirectPath = frontEndRedirect
                frontEndRedirect = ""
                delay(200L)
                navigateFunction.invoke(redirectPath)
            }
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