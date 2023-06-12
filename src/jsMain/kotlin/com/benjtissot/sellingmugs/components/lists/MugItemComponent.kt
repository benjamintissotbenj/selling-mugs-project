package com.benjtissot.sellingmugs.components.lists

import com.benjtissot.sellingmugs.entities.Mug
import csstype.AlignContent
import csstype.rem
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

        div {
            +"${props.mug.name} costs £${props.mug.price}"
        }
        img {
            css {
                width = 8.rem
                height = 8.rem
                padding = 1.rem
            }
            src = props.mug.artwork.imageURL
        }
        onClick = {props.onItemClick(props.mug)}
    }


}