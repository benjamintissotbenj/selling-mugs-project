package com.benjtissot.sellingmugs.components.lists

import com.benjtissot.sellingmugs.entities.Mug
import csstype.*
import emotion.react.css
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.img


external interface MugItemProps: Props {
    var mug: Mug
    var onItemClick: (Mug) -> Unit
}

val MugItemComponent = FC<MugItemProps> {
        props ->
    div {
        css {
            alignContent = AlignContent.center
            width = 10.rem
            // height = 10.rem
            padding = 1.rem
        }
        img {
            css {
                width = 8.rem
                height = 8.rem
                padding = 1.rem
            }
            src = props.mug.artwork.previewURLs.let {if (it.isNotEmpty()) it[0] else props.mug.artwork.imageURL}
        }

        div {
            css {
                width = 100.pct
                boxSizing = BoxSizing.borderBox
                padding = 1.vw
                display = Display.flex
                flexDirection = FlexDirection.column
                alignItems = AlignItems.start
            }
            div {
                css {
                    textOverflow = TextOverflow.ellipsis
                    whiteSpace = WhiteSpace.nowrap
                }
                +props.mug.name
            }
            div {
                +"£${props.mug.price}"
            }
        }
        onClick = {props.onItemClick(props.mug)}
    }


}
