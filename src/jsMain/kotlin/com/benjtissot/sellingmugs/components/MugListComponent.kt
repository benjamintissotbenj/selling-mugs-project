package com.benjtissot.sellingmugs.components

import com.benjtissot.sellingmugs.entities.Mug
import com.benjtissot.sellingmugs.getMugList
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import react.FC
import react.dom.html.ReactHTML.p
import react.useEffectOnce
import react.useState
import emotion.react.css
import csstype.Position
import csstype.px
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.ul

external interface MugListProps: Props {
    var list: List<Mug>
}

val mugListComponent = FC<MugListProps> {
        props ->
        ul {
            for (mug in props.list){
                div {
                    css {
                        position = Position.relative

                        top = 10.px
                        right = 10.px
                    }
                    +"${mug.name} costs £${mug.price}"
                }
            }
        }


}