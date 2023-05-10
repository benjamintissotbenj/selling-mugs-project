package com.benjtissot.sellingmugs.pages

import com.benjtissot.sellingmugs.components.FooterComponent
import com.benjtissot.sellingmugs.getUserInfo
import com.benjtissot.sellingmugs.jsonClient
import io.ktor.util.logging.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import mui.icons.material.Refresh
import mui.material.IconButton
import react.*
import react.dom.html.ReactHTML.div

private val LOG = KtorSimpleLogger("UserInfoPage.kt")

external interface UserInfoPageProps : Props {
}

private val scope = MainScope()

val UserInfoPage = FC<UserInfoPageProps> { props ->

    var message by useState("")

    useEffectOnce {
        scope.launch {
            message = getUserInfo()
        }
    }

    div {
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

    FooterComponent {}
}