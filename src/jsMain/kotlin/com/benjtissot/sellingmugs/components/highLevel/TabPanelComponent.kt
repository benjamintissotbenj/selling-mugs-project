@file:Suppress("UNUSED_EXPRESSION")

package com.benjtissot.sellingmugs.components.highLevel

import com.benjtissot.sellingmugs.fontNormalPlus
import csstype.*
import emotion.react.css
import mui.lab.TabContext
import mui.material.Box
import mui.material.Tab
import mui.material.Tabs
import mui.material.TabsVariant
import mui.system.sx
import react.FC
import react.PropsWithChildren
import react.create
import react.dom.aria.ariaControls
import react.dom.html.ReactHTML.div
import react.useState


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
                        val intValue = newValue as Int
                        props.onClickTab(intValue)
                        tabValue = intValue
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

