package com.benjtissot.sellingmugs.Pages

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.*
import com.benjtissot.sellingmugs.entities.Artwork
import com.benjtissot.sellingmugs.entities.Mug
import com.benjtissot.sellingmugs.entities.Session
import io.ktor.client.call.*
import io.ktor.http.*
import io.ktor.util.logging.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import mui.icons.material.Person
import mui.material.IconButton
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.useEffectOnce
import react.useState

val LOG = KtorSimpleLogger("UserInfoPage.kt")

external interface UserInfoPageProps : Props {
}

private val scope = MainScope()

val UserInfoPage = FC<UserInfoPageProps> { props ->
    var session: Session? by useState(null)
    var token: String by useState("EMPTY")
    LOG.debug("Rendering UserInfoPage")
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
                    postDummyLogin()
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