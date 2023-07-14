package com.benjtissot.sellingmugs.components.highLevel

import com.benjtissot.sellingmugs.AlertState
import com.benjtissot.sellingmugs.fontBig
import com.benjtissot.sellingmugs.fontNormalPlus
import com.benjtissot.sellingmugs.hideAlert
import csstype.*
import emotion.react.css
import mui.lab.TabContext
import mui.lab.TabPanel
import mui.material.*
import mui.system.sx
import react.*
import react.dom.aria.ariaControls
import react.dom.html.ReactHTML.div


external interface CreateTabsProps : PropsWithChildren {
    var labels : List<String>
    var height: Height
    var width: Width
    var onClickTab: (Int) -> Unit
}


val CreateTabsComponent = FC <CreateTabsProps> { props ->

    var tabValue by useState(0)
    val numberOfTabs = props.labels.size

    div {
        css {
            overflow = "auto".unsafeCast<Overflow>()
            boxSizing = BoxSizing.borderBox
            height = props.height
            width = props.width
        }

        TabContext {

            value = tabValue.toString()

            Box {
                sx {
                    "{ borderBottom: 1, borderColor: 'divider' }"
                }
                Tabs {
                    variant = TabsVariant.fullWidth
                    value = tabValue
                    onChange = { _, newValue ->
                        props.onClickTab(newValue as Int)
                        tabValue = newValue as Int
                    }
                    for (i: Int in 0 until numberOfTabs){
                        Tab {
                            css {
                                boxSizing = BoxSizing.borderBox
                                width = (100f/numberOfTabs).pct
                                height = 5.pct
                                minHeight = 40.px
                            }
                            label = div.create{
                                css {
                                    fontNormalPlus()
                                    textTransform = "initial".unsafeCast<TextTransform>()
                                    fontWeight = FontWeight.bold
                                }
                                +props.labels[i]
                            }
                            ariaControls = "simple-tabpanel-$i"
                        }
                    }
                }
            }

            // Render children tabs
            div {
                css {
                    boxSizing = BoxSizing.borderBox
                    width = 100.pct
                    height = 95.pct
                }
                +props.children
            }
        }
    }
}

