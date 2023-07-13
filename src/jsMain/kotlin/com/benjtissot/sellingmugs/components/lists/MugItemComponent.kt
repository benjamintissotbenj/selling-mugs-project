package com.benjtissot.sellingmugs.components.lists

import com.benjtissot.sellingmugs.components.createProduct.SweepImageComponent
import com.benjtissot.sellingmugs.entities.Mug
import com.benjtissot.sellingmugs.fontNormal
import com.benjtissot.sellingmugs.fontSmall
import csstype.*
import emotion.react.css
import mui.icons.material.AddShoppingCart
import mui.material.IconButton
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.events.Event
import react.*
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.img

external interface MugDetailsProps: Props {
    var mug: Mug
}
external interface MugItemListProps: MugDetailsProps {
    var onMouseEnterItem: (Mug, HTMLDivElement) -> Unit
}

val MugItemListComponent = FC<MugItemListProps> {
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
            height = 15.rem
            padding = 1.rem
        }
        div {
            img {
                css {
                    width = 8.rem
                    height = 8.rem
                    margin = 1.rem
                    boxSizing = BoxSizing.borderBox
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
                    width = 100.pct
                    paddingInline = 5.pct
                    boxSizing = BoxSizing.borderBox
                    textOverflow = TextOverflow.ellipsis
                    overflow = Overflow.hidden
                    whiteSpace = WhiteSpace.nowrap
                }
                +props.mug.name
            }
            div {
                css {
                    padding = 5.pct
                }
                +"£${props.mug.price}"
            }
        }
    }


}

external interface MugItemGridProps: MugDetailsProps {
    var onClickAddToCart: (Mug) -> Unit
}

val MugItemGridComponent = FC<MugItemGridProps> {
        props ->

    var hover by useState(false)

    div {
        css {
            display = Display.flex
            flexDirection = FlexDirection.column
            alignContent = AlignContent.center
            width = 90.pct
            height = (1.5* this.width as Percentage).unsafeCast<Height>()
            margin = 5.pct
            boxSizing = BoxSizing.borderBox
        }
        onMouseLeave = {
            hover = false
        }

        if (hover) {
            MugDetailsHover {
                mug = props.mug
                onClickAddToCart = props.onClickAddToCart
            }
        } else {
            MugDetailsDefault {
                mug = props.mug
                onMouseEnter = {
                    hover = true
                }
            }
        }
    }
}

external interface MugItemDefaultProps: MugDetailsProps {
    var onMouseEnter : () -> Unit
}

val MugDetailsDefault = FC<MugItemDefaultProps> { props ->
    img {
        css {
            width = 90.pct
            height = 90.pct
            margin = 3.pct
            boxSizing = BoxSizing.borderBox
        }
        src = props.mug.getBestPictureSrc()
        onMouseEnter = {
            props.onMouseEnter()
        }
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
                width = 100.pct
                paddingInline = 5.pct
                boxSizing = BoxSizing.borderBox
                textOverflow = TextOverflow.ellipsis
                overflow = Overflow.hidden
                whiteSpace = WhiteSpace.nowrap
            }
            +props.mug.name
        }
        div {
            css {
                padding = 5.pct
            }
            +"£${props.mug.price}"
        }
    }
}

external interface MugItemHoverProps: MugItemGridProps {
    var onMouseEnter : () -> Unit
}

val MugDetailsHover = FC<MugItemGridProps> { props ->

    SweepImageComponent {
        width = 90.pct
        height = 90.pct
        srcList = props.mug.getAllPictureSrcs()
        refresh = false
    }

    div {
        +props.mug.name
    }
    div {
        css {
            fontSmall()
        }
        +props.mug.description
    }
    div {
        +"£${props.mug.price}"
    }
    IconButton {
        AddShoppingCart()
        div {
            css {
                fontNormal()
                margin = 2.vw
            }
            +"Add to cart"
        }
        onClick = {
            props.onClickAddToCart(props.mug)
        }
    }
}