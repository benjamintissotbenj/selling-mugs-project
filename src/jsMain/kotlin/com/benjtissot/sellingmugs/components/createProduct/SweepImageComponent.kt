package com.benjtissot.sellingmugs.components.createProduct

import com.benjtissot.sellingmugs.contentCenteredVertically
import csstype.*
import emotion.react.css
import mui.icons.material.ChevronLeft
import mui.icons.material.ChevronRight
import mui.material.IconButton
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
}

val SweepImageComponent = FC<SweepImageProps> { props ->
    var index by useState(0)
    val sweep = props.srcList.isNotEmpty()

    div {
        css {
            contentCenteredVertically()
        }

        if (sweep) {
            IconButton {
                ChevronLeft()
                onClick = {
                    index = if (index>0) index - 1 else props.srcList.size - 1
                }
            }
            div {
                css {
                    position = Position.relative
                    height = props.height
                    width = props.width
                }
                for (i: Int in 0 until props.srcList.size){
                    img {
                        css {
                            height = props.height
                            width = props.width
                            position = Position.absolute
                            right = 0.px
                            top = 0.px
                            left = 0.px
                            bottom = 0.px
                            visibility = if (i == index) Visibility.visible else Visibility.collapse
                        }
                        src = "${props.srcList[i]}?${random.nextInt()}"
                    }
                }
            }

            IconButton {
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