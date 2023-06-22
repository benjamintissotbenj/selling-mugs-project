package com.benjtissot.sellingmugs.pages

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.buttons.LogoutButtonComponent
import com.benjtissot.sellingmugs.components.highLevel.CreateTabsComponent
import csstype.pct
import csstype.vh
import emotion.react.css
import io.ktor.util.logging.*
import kotlinx.coroutines.launch
import mui.lab.TabContext
import mui.lab.TabPanel
import mui.material.Box
import mui.material.Tab
import mui.material.Tabs
import mui.system.sx
import react.FC
import react.create
import react.dom.aria.ariaControls
import react.dom.html.ReactHTML.div
import react.useEffectOnce
import react.useState

private val LOG = KtorSimpleLogger("UserInfoPage.kt")


@Suppress("UNUSED_EXPRESSION")
val UserInfoPage = FC<NavigationProps> { props ->

    useEffectOnce {
        scope.launch {
        }
    }

    div {

    }

    CreateTabsComponent {
        labels = listOf("Tab 1", "Tab 2", "Tab 3")

        TabPanel {
            value = "0"
            div {
                +"First tab panel is selected"
            }
        }

        TabPanel {
            value = "1"
            div {
                +"Second tab panel is selected"
            }
        }

        TabPanel {
            value = "2"
            div {
                +"Third tab panel is selected"
            }
        }
    }


    LogoutButtonComponent {
        session = props.session
        updateSession = props.updateSession
        navigate = props.navigate
    }

}

