package com.benjtissot.sellingmugs.components

import com.benjtissot.sellingmugs.entities.Mug
import csstype.*
import react.FC
import emotion.react.css
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.header
import react.dom.html.ReactHTML.footer
import react.dom.html.ReactHTML.img



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
            overflowX = Overflow.scroll
            scrollBehavior = ScrollBehavior.smooth
            paddingBlock = 1.rem
            maxWidth = 100.rem
        }
        props.list.forEach { mugItm ->
            MugItemComponent {
                mug = mugItm
                onItemClick = props.onItemClick
            }
        }

    }


}