package com.benjtissot.sellingmugs.pages

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.highLevel.FooterComponent
import com.benjtissot.sellingmugs.components.highLevel.NavigationBarComponent
import emotion.react.css
import io.ktor.client.call.*
import io.ktor.http.*
import io.ktor.util.logging.*
import kotlinx.coroutines.launch
import react.FC
import react.dom.html.ReactHTML.div
import react.router.useNavigate
import react.useEffectOnce
import react.useState

private val LOG = KtorSimpleLogger("BasicPage.kt")

external interface BasicPageProps : SessionPageProps {
    var internalPage : FC<NavigationProps>
}

val BasicPage = FC<BasicPageProps> { props ->

    val navigate = useNavigate()

    NavigationBarComponent {
        session = props.session
        updateSession = props.updateSession
        this.navigate = navigate
        this.setAlert = props.setAlert
    }

    props.internalPage {
        session = props.session
        updateSession = props.updateSession
        this.navigate = navigate
        setAlert = props.setAlert
    }

    FooterComponent {}
}