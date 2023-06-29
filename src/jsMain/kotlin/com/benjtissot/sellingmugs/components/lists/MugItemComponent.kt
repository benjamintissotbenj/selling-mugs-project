package com.benjtissot.sellingmugs.components.lists

import com.benjtissot.sellingmugs.entities.Mug
import csstype.*
import emotion.react.css
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.events.Event
import react.*
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.img

val mugItemHeight = 15.rem
external interface MugItemProps: Props {
    var mug: Mug
    var onItemClick: (Mug) -> Unit
    var onMouseEnterItem: (Mug, HTMLDivElement) -> Unit
}

val MugItemComponent = FC<MugItemProps> {
        props ->

    val hoverZoneRef = useRef<Element>(null)

    useEffect {
        val hoverZone = hoverZoneRef.current

        val handleMouseEnter: (Event) -> Unit = { event ->
            event.preventDefault()
            props.onMouseEnterItem(props.mug, event.currentTarget as HTMLDivElement)
        }

        hoverZone?.addEventListener("mouseenter", handleMouseEnter)
    }

    div {
        css {
            alignContent = AlignContent.center
            width = 10.rem
            height = mugItemHeight
            padding = 1.rem
        }
        div {
            img {
                css {
                    width = 8.rem
                    height = 8.rem
                    margin = 1.rem
                }
                src = props.mug.getBestPictureSrc()
            }
            ref = hoverZoneRef
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
