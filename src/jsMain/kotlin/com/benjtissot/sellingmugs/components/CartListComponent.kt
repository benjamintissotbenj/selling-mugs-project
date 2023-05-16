package com.benjtissot.sellingmugs.components

import com.benjtissot.sellingmugs.MugCartItem
import com.benjtissot.sellingmugs.entities.Mug
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
        +props.title
    }
    div {
        css {
            display = Display.flex
            flexDirection = FlexDirection.column
            overflowY = Overflow.scroll
            scrollBehavior = ScrollBehavior.smooth
            paddingBlock = 1.rem
            maxWidth = 80.rem
            maxHeight = 60.vh
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