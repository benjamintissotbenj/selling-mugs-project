package com.benjtissot.sellingmugs.pages

import com.benjtissot.sellingmugs.components.FooterComponent
import io.ktor.util.logging.*
import kotlinx.coroutines.MainScope
import react.FC
import react.Props
import react.dom.html.ReactHTML.div

private val LOG = KtorSimpleLogger("UserInfoPage.kt")

external interface UserInfoPageProps : Props {
}

private val scope = MainScope()

val UserInfoPage = FC<UserInfoPageProps> { props ->

    div {
        +"Hello User Info Component"
    }

    FooterComponent {}
}