package com.benjtissot.sellingmugs

import com.benjtissot.sellingmugs.components.LoginFormComponent
import com.benjtissot.sellingmugs.components.NavigationBarComponent
import csstype.*
import emotion.react.css
import io.ktor.client.call.*
import io.ktor.http.*
import io.ktor.util.logging.*
import kotlinx.coroutines.launch
import mui.icons.material.Person
import mui.material.IconButton
import org.komputing.khash.sha256.extensions.sha256
import react.FC
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.router.useNavigate

private val LOG = KtorSimpleLogger("loginPage.kt")

external interface LoginPageProps : SessionPageProps {
}

val LoginPage = FC<RegisterPageProps> { props ->
    val navigateLogin = useNavigate()
    NavigationBarComponent {
        session = props.session
        updateSession = props.updateSession
        navigate = navigateLogin
    }

    div {
            +"Login Page"
        }

    div {

        css {
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
                    if (httpResponse.status == HttpStatusCode.OK) {
                        // Using local variable because otherwise update is not atomic
                        val tokenString = httpResponse.body<String>()
                        LOG.debug("Creating new client with new token $tokenString")
                        updateClientWithToken(tokenString)
                        navigateLogin.invoke(HOMEPAGE_PATH)
                    } else {
                        LOG.error("Not valid login")
                    }
                    props.updateSession()
                }
            }
        }

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


    IconButton{
        div {
            +"Logout"
        }
        Person()
        onClick = {
            LOG.debug("Click on Logout")
            scope.launch {
                val httpResponse = logout()

                LOG.debug("After register, response is $httpResponse")
                if (httpResponse.status == HttpStatusCode.OK){
                    LOG.debug("Logged out")
                    navigateLogin.invoke(HOMEPAGE_PATH)
                } else {
                    LOG.error("Logout not working")
                }
                props.updateSession()
            }
        }
    }
}