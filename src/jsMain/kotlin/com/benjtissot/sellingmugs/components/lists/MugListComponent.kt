package com.benjtissot.sellingmugs.components.lists

import com.benjtissot.sellingmugs.components.popups.MugDetailsPopup
import com.benjtissot.sellingmugs.entities.Mug
import com.benjtissot.sellingmugs.fontBig
import csstype.*
import emotion.react.css
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLDivElement
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.header
import react.useState


external interface MugListProps: Props {
    var list: List<Mug>
    var title: String
    var onItemClick: (Mug) -> Unit
}

val MugListComponent = FC<MugListProps> {
        props ->


    var popupTarget : HTMLDivElement? by useState(null)
    var mugShowDetails : Mug? by useState(null)

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
                this.onMouseEnterItem = { mug, target ->
                    mugShowDetails = mug
                    popupTarget = target
                }
                this.onMouseLeaveItem = {
                    mugShowDetails = null
                    popupTarget = null
                }
            }
        }

    }

    MugDetailsPopup {
        this.popupTarget = popupTarget
    }


}