package com.benjtissot.sellingmugs.pages

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.forms.RegisterFormComponent
import csstype.*
import emotion.react.css
import io.ktor.client.call.*
import io.ktor.http.*
import io.ktor.util.logging.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import react.FC
import react.dom.html.ReactHTML
import react.dom.html.ReactHTML.div

private val LOG = KtorSimpleLogger("loginPage.kt")


val RegisterPage = FC<NavigationProps> { props ->

    div {
        css {
            contentCenteredHorizontally()
        }
        // Creating register form
        RegisterFormComponent {
            onSubmit = { registerInfo ->
                scope.launch {
                    val httpResponse = register(registerInfo)
                    when (httpResponse.status) {
                        HttpStatusCode.OK -> {
                            props.updateSession()
                            // User is now registered, token is in response
                            // Using local variable because otherwise update is not atomic
                            val tokenString = httpResponse.body<String>()
                            LOG.debug("Creating new client with new token $tokenString")
                            updateClientWithToken(tokenString)
                            if (frontEndRedirect.isEmpty()){
                                props.navigate.invoke(HOMEPAGE_PATH)
                            } else {
                                val redirectPath = frontEndRedirect
                                frontEndRedirect = ""
                                delay(200L)
                                props.navigate.invoke(redirectPath)
                            }
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
                    props.navigate.invoke(LOGIN_PATH)
                }
            }
        }
    }
}