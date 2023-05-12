package com.benjtissot.sellingmugs

import com.benjtissot.sellingmugs.components.FooterComponent
import com.benjtissot.sellingmugs.components.NavigationBarComponent
import io.ktor.http.*
import io.ktor.util.logging.*
import kotlinx.coroutines.launch
import mui.icons.material.Refresh
import mui.material.IconButton
import react.FC
import react.dom.html.ReactHTML.div
import react.router.useNavigate
import react.useEffectOnce
import react.useState

private val LOG = KtorSimpleLogger("UserInfoPage.kt")

external interface UserInfoPageProps : SessionPageProps {
}

val UserInfoPage = FC<UserInfoPageProps> { props ->

    val navigateLogin = useNavigate()
    NavigationBarComponent {
        session = props.session
        updateSession = props.updateSession
        navigate = navigateLogin
    }

    var message by useState("")

    useEffectOnce {
        scope.launch {
            message = getUserInfo()
        }
    }

    div {
        divDefaultCss()
        +"Hello User Info Component"
        +"Extra message $message"
    }

    IconButton {
        Refresh()
        onClick = {
            scope.launch {
                message = getUserInfo()
            }
        }
    }

    LogoutButtonComponent {
        session = props.session
        updateSession = props.updateSession
        navigate = navigateLogin
    }


    FooterComponent {}
}


external interface LogoutButtonProps : NavigationProps {
}

val LogoutButtonComponent = FC<LogoutButtonProps> { props ->
    IconButton{
    div {
        +"Logout"
    }
    mui.icons.material.Person()
    onClick = {
        LOG.debug("Click on Logout")
        scope.launch {
            val httpResponse = logout()

            LOG.debug("After register, response is $httpResponse")
            if (httpResponse.status == HttpStatusCode.OK){
                LOG.debug("Logged out")
                props.navigate.invoke(HOMEPAGE_PATH)
            } else {
                LOG.error("Logout not working")
            }
            props.updateSession()
        }
    }
}
}
