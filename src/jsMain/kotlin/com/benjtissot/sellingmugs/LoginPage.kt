package com.benjtissot.sellingmugs

import com.benjtissot.sellingmugs.components.*
import com.benjtissot.sellingmugs.entities.Session
import io.ktor.client.call.*
import io.ktor.http.*
import io.ktor.util.logging.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import mui.icons.material.Person
import mui.material.IconButton
import org.komputing.khash.sha256.Sha256
import org.komputing.khash.sha256.extensions.sha256
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.router.useNavigate
import react.useEffectOnce
import react.useState

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

    div {
            +"Login Page"
        }

        // Creating login form

        LoginFormComponent{
            onSubmit = { email, clearPassword ->
                val hashedPassword = clearPassword.sha256().toString()
                scope.launch{
                    val httpResponse = login(email, hashedPassword)
                    if (httpResponse.status == HttpStatusCode.OK){
                        // Using local variable because otherwise update is not atomic
                        val tokenString = httpResponse.body<String>()
                        LOG.debug("Creating new client with new token $tokenString")
                        updateClientWithToken(tokenString)
                    } else {
                        LOG.error("Not valid login")
                    }
                    props.updateSession()
                }
            }
    }

    /*IconButton{
        div {
            +"Login"
        }
        Person()
        onClick = {
            scope.launch {
                val httpResponse = postDummyLogin()
                if (httpResponse.status == HttpStatusCode.OK){
                    // Using local variable because otherwise update is not atomic
                    val tokenString = httpResponse.body<String>()
                    LOG.debug("Creating new client with new token $tokenString")
                    updateClientWithToken(tokenString)
                } else {
                    LOG.error("Not valid login")
                }
                props.updateSession()
            }
        }
    }*/

    IconButton{
        div {
            +"Register"
        }
        Person()
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