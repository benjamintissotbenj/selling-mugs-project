package com.benjtissot.sellingmugs.components.lists

import com.benjtissot.sellingmugs.components.popups.MugDetailsPopup
import com.benjtissot.sellingmugs.entities.Mug
import com.benjtissot.sellingmugs.fontBig
import csstype.*
import emotion.react.css
import org.w3c.dom.Element
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.events.Event
import react.*
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.header


external interface MugListProps: Props {
    var list: List<Mug>
    var title: String
    var onItemClick: (Mug) -> Unit
    var onMouseEnterItem: (Mug, HTMLDivElement) -> Unit
}

val MugListComponent = FC<MugListProps> {
        props ->


    header {
        css {
            width = 100.pct
        }
        div {
            css {
                fontBig()
                marginLeft = 10.vw
            }
            +props.title
        }
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
                this.onMouseEnterItem = props.onMouseEnterItem
            }
        }

    }


}