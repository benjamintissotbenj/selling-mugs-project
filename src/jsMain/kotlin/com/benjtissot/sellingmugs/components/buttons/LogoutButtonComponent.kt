package com.benjtissot.sellingmugs.components.buttons

import com.benjtissot.sellingmugs.HOMEPAGE_PATH
import com.benjtissot.sellingmugs.NavigationProps
import com.benjtissot.sellingmugs.logout
import com.benjtissot.sellingmugs.scope
import csstype.*
import emotion.react.css
import io.ktor.http.*
import io.ktor.util.logging.*
import kotlinx.coroutines.launch
import mui.icons.material.PersonRemove
import mui.material.IconButton
import react.FC
import react.dom.html.ReactHTML


private val LOG = KtorSimpleLogger("LogoutButtonComponent.kt")

external interface LogoutButtonProps : NavigationProps {
}

val LogoutButtonComponent = FC<LogoutButtonProps> { props ->
    IconButton{
        css {
            boxSizing = BoxSizing.borderBox
            padding = 0.px
            minHeight = 20.px
        }
        ReactHTML.div {
            +"Logout"
        }
        PersonRemove()
        onClick = {
            scope.launch {
                val httpResponse = logout()

                LOG.debug("After logout, response is $httpResponse, updating session")
                props.updateSession()
                if (httpResponse.status == HttpStatusCode.OK){
                    LOG.debug("Logged out")
                    props.navigate.invoke(HOMEPAGE_PATH)
                } else {
                    LOG.error("Logout not working")
                }
            }
        }
    }
}
