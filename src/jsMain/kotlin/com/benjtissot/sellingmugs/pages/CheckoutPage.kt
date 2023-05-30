package com.benjtissot.sellingmugs.pages

import com.benjtissot.sellingmugs.LOGIN_PATH
import com.benjtissot.sellingmugs.SessionPageProps
import com.benjtissot.sellingmugs.components.NavigationBarComponent
import com.benjtissot.sellingmugs.isLoggedIn
import com.benjtissot.sellingmugs.scope
import io.ktor.client.call.*
import io.ktor.http.*
import io.ktor.util.logging.*
import kotlinx.coroutines.launch
import react.FC
import react.dom.html.ReactHTML.div
import react.router.useNavigate
import react.useEffectOnce
import react.useState

private val LOG = KtorSimpleLogger("CheckoutPage.kt")

external interface CheckoutPageProps: SessionPageProps {
}

val CheckoutPage = FC<SessionPageProps> { props ->
    val navigateCheckout = useNavigate()
    var loggedIn by useState(false)

    useEffectOnce {
        scope.launch {
            val loggedInResponse = isLoggedIn()
            val loggedInValue = loggedInResponse.status != HttpStatusCode.Unauthorized && (loggedInResponse.body<String>() == "true")

            LOG.debug("User is logged in with response: ${loggedInResponse.status != HttpStatusCode.Unauthorized} && ${loggedInResponse.body<Boolean>()} : $loggedInValue")
            if (!loggedInValue) {
                navigateCheckout.invoke(LOGIN_PATH)
            }
            loggedIn = loggedInValue
        }
    }

    NavigationBarComponent {
        session = props.session
        updateSession = props.updateSession
        navigate = navigateCheckout
    }

    if (loggedIn){
        div {
            +"Hello Checkout Component"
        }
    }

}