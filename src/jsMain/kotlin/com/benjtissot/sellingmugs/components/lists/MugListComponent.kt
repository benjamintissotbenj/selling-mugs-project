package com.benjtissot.sellingmugs.components.lists

import com.benjtissot.sellingmugs.entities.Mug
import csstype.*
import emotion.react.css
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.header


external interface MugListProps: Props {
    var list: List<Mug>
    var title: String
    var onItemClick: (Mug) -> Unit
}

val MugListComponent = FC<MugListProps> {
        props ->
    header {
        +props.title
    }
    div {
        css {
            display = Display.flex
            overflowX = "auto".unsafeCast<Overflow>()
            scrollBehavior = ScrollBehavior.smooth
            paddingBlock = 1.rem
        }
        props.list.forEach { mugItm ->
            MugItemComponent {
                mug = mugItm
                onItemClick = props.onItemClick
            }
        }

    }


}