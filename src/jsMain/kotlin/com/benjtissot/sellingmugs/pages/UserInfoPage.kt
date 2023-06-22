package com.benjtissot.sellingmugs.pages

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.buttons.LogoutButtonComponent
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

    var tabValue by useState(0)

    useEffectOnce {
        scope.launch {
        }
    }

    div {

    }
    TabContext {

        value = tabValue.toString()

        Box {
            sx {
                "{ borderBottom: 1, borderColor: 'divider' }"
            }
            Tabs {
                value = tabValue
                onChange = { _, newValue ->
                    tabValue = newValue
                }
                Tab {
                    css {
                        width = 30.pct
                        height = 10.vh
                    }
                    label = div.create{+"Tab 1"}
                    ariaControls = "simple-tabpanel-0"
                }
                Tab {
                    css {
                        width = 30.pct
                        height = 10.vh
                    }
                    label = div.create{+"Tab 2"}
                    ariaControls = "simple-tabpanel-1"
                }
                Tab {
                    css {
                        width = 30.pct
                        height = 10.vh
                    }
                    label = div.create{+"Tab 3"}
                    ariaControls = "simple-tabpanel-2"
                }
            }
        }

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

