package com.benjtissot.sellingmugs.Pages

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.FooterComponent
import com.benjtissot.sellingmugs.components.InputComponent
import com.benjtissot.sellingmugs.components.MugListComponent
import com.benjtissot.sellingmugs.components.NavigationBarComponent
import com.benjtissot.sellingmugs.entities.Artwork
import com.benjtissot.sellingmugs.entities.Mug
import com.benjtissot.sellingmugs.entities.Session
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.useEffectOnce
import react.useState

external interface UserInfoPageProps : Props {
}

private val scope = MainScope()

val UserInfoPage = FC<UserInfoPageProps> { props ->
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
            +"User Info Page"
            +"${session!!.user?.email}"
        }

    } ?:

    FooterComponent {}
}