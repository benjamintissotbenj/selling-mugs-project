package com.benjtissot.sellingmugs.components.lists

import com.benjtissot.sellingmugs.MugCartItem
import com.benjtissot.sellingmugs.fontBig
import csstype.*
import emotion.react.css
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.header


external interface CartListProps: Props {
    var list: List<MugCartItem>
    var title: String
    var onRemoveItem: (MugCartItem) -> Unit
}

val CartListComponent = FC<CartListProps> {
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
            flexDirection = FlexDirection.column
            overflowY = "auto".unsafeCast<Overflow>()
            scrollBehavior = ScrollBehavior.smooth
            paddingBlock = 1.rem
            width = 80.vw
            maxWidth = 80.rem
            maxHeight = 70.vh
        }
        props.list.forEach { mugCartItm ->
            MugCartItemComponent {
                mugCartItem = mugCartItm
                onRemove = { mugCartItem ->
                    props.onRemoveItem(mugCartItem)
                }
            }
        }

    }


}