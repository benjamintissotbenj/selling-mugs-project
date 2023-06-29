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
    var onMouseLeaveItem: () -> Unit
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
        val handleMouseLeave: (Event) -> Unit = { event ->
            event.preventDefault()
            props.onMouseLeaveItem()
        }

        hoverZone?.addEventListener("mouseenter", handleMouseEnter)
        hoverZone?.addEventListener("mouseleave", handleMouseLeave)
    }

    div {
        css {
            alignContent = AlignContent.center
            width = 10.rem
            height = mugItemHeight
            padding = 1.rem
        }
        ref = hoverZoneRef
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
