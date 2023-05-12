package com.benjtissot.sellingmugs

import com.benjtissot.sellingmugs.components.NavigationBarComponent
import com.benjtissot.sellingmugs.components.RegisterFormComponent
import csstype.AlignItems
import csstype.Display
import csstype.FlexDirection
import emotion.react.css
import io.ktor.client.call.*
import io.ktor.http.*
import io.ktor.util.logging.*
import kotlinx.coroutines.launch
import react.FC
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

    }
}