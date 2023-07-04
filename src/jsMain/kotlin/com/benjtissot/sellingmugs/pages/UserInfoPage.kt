package com.benjtissot.sellingmugs.pages

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.UserInfoComponent
import com.benjtissot.sellingmugs.components.buttons.LogoutButtonComponent
import com.benjtissot.sellingmugs.components.highLevel.CreateTabsComponent
import com.benjtissot.sellingmugs.components.lists.UserOrderListComponent
import csstype.*
import emotion.react.css
import io.ktor.util.logging.*
import kotlinx.coroutines.launch
import mui.lab.TabContext
import mui.lab.TabPanel
import mui.lab.TabPanelClasses
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

    CreateTabsComponent {
        height = 94.pct
        width = 100.pct
        labels = listOf("User Information", "Orders")
        onClickTab = { value ->
            when (value) {
                0 -> scope.launch {
                        recordClick(props.session.clickDataId, Const.ClickType.USER_INFO_TAB.type)
                    }
                1 -> scope.launch {
                        recordClick(props.session.clickDataId, Const.ClickType.USER_INFO_ORDER_TAB.type)
                    }
            }
        }

        TabPanel {
            css {
                boxSizing = BoxSizing.borderBox
                width = 100.pct
                height = 100.pct
            }
            value = "0"
            UserInfoComponent {
                user = props.session.user!!
            }
        }

        TabPanel {
            css {
                tabPanel()
            }
            value = "1"
            UserOrderListComponent {
                userId = props.session.user!!.id
                setAlert = props.setAlert
            }

        }
    }

    div {
        css {
            display = Display.flex
            flexDirection = FlexDirection.rowReverse
            height = 6.pct
        }
        LogoutButtonComponent {
            session = props.session
            updateSession = props.updateSession
            navigate = props.navigate
        }
    }

}

