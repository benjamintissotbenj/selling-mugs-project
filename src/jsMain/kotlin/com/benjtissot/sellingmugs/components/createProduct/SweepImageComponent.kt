package com.benjtissot.sellingmugs.components.createProduct

import com.benjtissot.sellingmugs.contentCenteredHorizontally
import com.benjtissot.sellingmugs.contentCenteredVertically
import csstype.*
import emotion.react.css
import mui.icons.material.ChevronLeft
import mui.icons.material.ChevronRight
import mui.material.IconButton
import mui.material.Size
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.img
import react.useState
import kotlin.random.Random

val random = Random(0)

external interface SweepImageProps: Props {
    var width: Width?
    var height: Height?
    var srcList: List<String>
    var refresh: Boolean
    var marginInline : MarginInline?
    var marginTop: MarginTop?
}

val SweepImageComponent = FC<SweepImageProps> { props ->
    var index by useState(0)
    val sweep = props.srcList.isNotEmpty() || index < 0

    div {
        css {
            contentCenteredVertically()
            justifyContent = JustifyContent.center
            props.width?.let { width = it }
            props.height?.let { height = it }
            props.marginInline?.let{ marginInline = it }
            props.marginTop?.let { marginTop = it }
            boxSizing = BoxSizing.borderBox
        }

        if (sweep) {
            IconButton {
                css {
                    width = 5.pct
                }
                size = Size.small
                ChevronLeft()
                onClick = {
                    index = if (index>0) index - 1 else props.srcList.size - 1
                }
            }
            div {
                css {
                    contentCenteredHorizontally()
                    position = Position.relative
                    height = 90.pct
                    width = 90.pct
                }
                for (i: Int in 0 until props.srcList.size){
                    img {
                        css {
                            position = Position.absolute
                            top = 0.px
                            bottom = 0.px
                            if (i == props.srcList.size - 1){
                                left = 0.px
                                right = 0.px
                                width = 100.pct
                            } else {
                                height = 100.pct
                            }
                            visibility = if (i == index) Visibility.visible else Visibility.collapse
                        }
                        src = props.srcList[i] + if (props.refresh) "?${random.nextInt()}" else ""
                    }
                }
            }

            IconButton {
                css {
                    width = 5.pct
                }
                size = Size.small
                ChevronRight()
                onClick = {
                    index = if (index<props.srcList.size - 1) index + 1 else 0
                }
            }
        } else {
            img {
                css {
                    height = props.height
                    width = props.width
                }
                src = "https://images.printify.com/api/catalog/5e440fbfd897db313b1987d1.jpg?s=320"
            }
        }
    }
}