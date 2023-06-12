package com.benjtissot.sellingmugs.pages

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.highLevel.FooterComponent
import com.benjtissot.sellingmugs.components.highLevel.NavigationBarComponent
import com.benjtissot.sellingmugs.components.lists.CartListComponent
import csstype.*
import emotion.react.css
import io.ktor.util.logging.*
import kotlinx.coroutines.launch
import mui.icons.material.Payment
import mui.material.IconButton
import react.FC
import react.dom.html.ReactHTML.div
import react.router.useNavigate
import react.useEffectOnce

private val LOG = KtorSimpleLogger("CustomMugPage.kt")

val CustomMugPage = FC<NavigationProps> { props ->

    useEffectOnce {
        scope.launch {

        }
    }

    div {
        +"Hello CustomMugPage"
    }

}
