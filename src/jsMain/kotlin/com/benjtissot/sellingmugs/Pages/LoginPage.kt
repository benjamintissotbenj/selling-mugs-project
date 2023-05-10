package com.benjtissot.sellingmugs.pages

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.*
import com.benjtissot.sellingmugs.entities.Session
import io.ktor.client.call.*
import io.ktor.http.*
import io.ktor.util.logging.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import mui.icons.material.Person
import mui.material.IconButton
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
    var token: String by useState("EMPTY")
    LOG.debug("Rendering loginPage")
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
            +"User Info Page"
            +token
        }

        IconButton{
            div {
                +"Login"
            }
            Person()
            onClick = {
                scope.launch {

                    LOG.debug("Click on Login")
                    val httpResponse = postDummyLogin()
                    LOG.debug("After login, response is $httpResponse")
                    if (httpResponse.status == HttpStatusCode.OK){
                        LOG.debug("Token is ${httpResponse.body<String>()}")
                        token = httpResponse.body<String>()
                        jsonClient = getClient(token)
                        LOG.debug("After login, client is $jsonClient")
                    } else {
                        token = "NOT WORKING"
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
                        token = httpResponse.body<String>()
                    } else {
                        token = "NOT WORKING"
                    }
                }
            }
        }

    } ?:

    FooterComponent {}
}