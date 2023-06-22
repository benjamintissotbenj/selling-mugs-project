package com.benjtissot.sellingmugs.components.highLevel

import com.benjtissot.sellingmugs.AlertState
import com.benjtissot.sellingmugs.hideAlert
import csstype.pct
import csstype.px
import csstype.vh
import emotion.react.css
import mui.lab.TabContext
import mui.lab.TabPanel
import mui.material.*
import mui.system.sx
import react.*
import react.dom.aria.ariaControls
import react.dom.html.ReactHTML.div


external interface CreateTabsProps : PropsWithChildren,
    react.dom.html.HTMLAttributes<org.w3c.dom.HTMLDivElement> {
    var labels : List<String>
}


val CreateTabsComponent = FC <CreateTabsProps> { props ->

    var tabValue by useState(0)
    val numberOfTabs = props.labels.size

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
                for (i: Int in 0 until numberOfTabs){
                    Tab {
                        css {
                            width = (100f/numberOfTabs).pct
                            height = 5.vh
                            minHeight = 40.px
                        }
                        label = div.create{+props.labels[i]}
                        ariaControls = "simple-tabpanel-$i"
                    }
                }
            }
        }

        // Render children tabs
        +props.children
    }
}

