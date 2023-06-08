package com.benjtissot.sellingmugs.pages

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.forms.RegisterFormComponent
import com.benjtissot.sellingmugs.components.highLevel.FooterComponent
import com.benjtissot.sellingmugs.components.highLevel.NavigationBarComponent
import csstype.*
import emotion.react.css
import io.ktor.client.call.*
import io.ktor.http.*
import io.ktor.util.logging.*
import kotlinx.coroutines.launch
import react.FC
import react.dom.html.ReactHTML
import react.dom.html.ReactHTML.div
import react.router.useNavigate

private val LOG = KtorSimpleLogger("loginPage.kt")

external interface RegisterPageProps : SessionPageProps {
}

val RegisterPage = FC<RegisterPageProps> { props ->
    val navigateRegister = useNavigate()

    NavigationBarComponent {
        session = props.session
        updateSession = props.updateSession
        navigate = navigateRegister
    }

    div {
        css {
            mainPageDiv()
            display = Display.flex
            flexDirection = FlexDirection.column
            alignItems = AlignItems.center
        }

        // Creating register form
        RegisterFormComponent {
            onSubmit = { user ->
                scope.launch {
                    val httpResponse = register(user)
                    when (httpResponse.status) {
                        HttpStatusCode.OK -> { // User is now registered, token is in response
                            // Using local variable because otherwise update is not atomic
                            val tokenString = httpResponse.body<String>()
                            LOG.debug("Creating new client with new token $tokenString")
                            updateClientWithToken(tokenString)
                            navigateRegister.invoke(HOMEPAGE_PATH)
                            props.updateSession()
                        }
                        HttpStatusCode.Conflict -> { // User already existed
                            LOG.error("User already exists")
                        }
                        else -> {
                            LOG.error("Something went wrong")
                        }
                    }
                }
            }
        }

        // Login invitation
        div {
            css {
                display = Display.flex
                flexDirection = FlexDirection.row
                alignItems = AlignItems.center
            }

            div {
                divDefaultCss()
                +"Already have an account? "
            }
            ReactHTML.button {
                css {
                    marginLeft = 2.vw
                    borderRadius = 2.vh
                    backgroundColor = NamedColor.transparent
                    fontSize = 2.vh
                }
                +"Login"
                onClick = {
                    LOG.debug("Click on Login")
                    navigateRegister.invoke(LOGIN_PATH)
                }
            }
        }

    }

    FooterComponent{}
}