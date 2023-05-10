package com.benjtissot.sellingmugs

import com.benjtissot.sellingmugs.components.FooterComponent
import com.benjtissot.sellingmugs.components.LoginFormComponent
import com.benjtissot.sellingmugs.components.NavigationBarComponent
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
import react.useEffectOnce
import react.useState

private val LOG = KtorSimpleLogger("loginPage.kt")

external interface LoginPageProps : Props {
}

private val scope = MainScope()

val LoginPage = FC<LoginPageProps> { props ->
    var session: Session? by useState(null)
    // At first initialisation, get the list
    // Alternative is useState when we want to persist something across re-renders
    useEffectOnce {
        scope.launch {
            session = getSession()
        }
    }
    session?.also{
        NavigationBarComponent {
            currentSession = session!!
            updateSession = {
                scope.launch {
                    session = getSession()
                }
            }
        }

        div {
            +"Login Page"
        }

        // Creating login form

        LoginFormComponent{
            onSubmit = { email, clearPassword ->
                val hashedPassword = clearPassword.sha256().toString()
                scope.launch{
                    login(email, hashedPassword)
                }
            }
        }

        IconButton{
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
                }
            }
        }

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
                }
            }
        }

    } ?:

    FooterComponent {}
}