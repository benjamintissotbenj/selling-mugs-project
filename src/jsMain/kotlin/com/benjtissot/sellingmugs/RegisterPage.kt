package com.benjtissot.sellingmugs

import com.benjtissot.sellingmugs.components.NavigationBarComponent
import com.benjtissot.sellingmugs.components.RegisterFormComponent
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
            +"Register Page"
        }

    div {

        css {
            display = Display.flex
            flexDirection = FlexDirection.column
            alignItems = AlignItems.center
        }

        // Creating register form
        RegisterFormComponent {
            onSubmit = { user ->
                scope.launch {
                    val httpResponse = register(user)
                    if (httpResponse.status == HttpStatusCode.OK) {
                        // Using local variable because otherwise update is not atomic
                        val tokenString = httpResponse.body<String>()
                        LOG.debug("Creating new client with new token $tokenString")
                        updateClientWithToken(tokenString)
                        navigateRegister.invoke(HOMEPAGE_PATH)
                    } else {
                        LOG.error("User already exists")
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
                +"Not a customer yet? "
            }
            button {
                css {
                    marginLeft = 2.vw
                    borderRadius = 2.vh
                    backgroundColor = NamedColor.transparent
                }
                +"Register now"
                onClick = {
                    LOG.debug("Click on Register")
                    scope.launch {
                        val httpResponse = postDummyRegister()

                        LOG.debug("After register, response is $httpResponse")
                        if (httpResponse.status == HttpStatusCode.OK){
                            LOG.debug("Token is ${httpResponse.body<String>()}")
                        } else {
                            LOG.error("Register not working")
                        }
                        props.updateSession()
                    }
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
                    navigateRegister.invoke(HOMEPAGE_PATH)
                } else {
                    LOG.error("Logout not working")
                }
                props.updateSession()
            }
        }
    }
}