package com.benjtissot.sellingmugs.components.lists

import com.benjtissot.sellingmugs.components.createProduct.SweepImageComponent
import com.benjtissot.sellingmugs.entities.local.Mug
import com.benjtissot.sellingmugs.fontNormal
import com.benjtissot.sellingmugs.fontSmall
import csstype.*
import emotion.react.css
import mui.icons.material.AddShoppingCart
import mui.lab.LoadingButton
import mui.material.Chip
import mui.material.ChipVariant
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
                +"£${props.mug.price}0"
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
            justifyContent = JustifyContent.center
            alignContent = AlignContent.center
            width = 90.pct
            minHeight = 55.vh
            height = 1.5 * 90.pct
            marginBlock = 2.pct
            marginInline = 5.pct
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

    div {
        css {
            position = Position.relative
            width = 96.pct
            height = 100.pct
            boxSizing = BoxSizing.borderBox
        }
        img {
            css {
                position = Position.absolute
                width = 100.pct
                right = 0.px
                top = 0.px
                left = 0.px
                bottom = 0.px
                objectFit = ObjectFit.contain
            }
            src = props.mug.getBestPictureSrc()
            onMouseEnter = {
                props.onMouseEnter()
            }
        }
        Chip {
            css {
                position = Position.absolute
                top = 2.pct
                left = 2.pct
            }
            variant = ChipVariant.outlined
            label = div.create { +props.mug.category.name }
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
                boxSizing = BoxSizing.borderBox
                textOverflow = TextOverflow.ellipsis
                overflow = Overflow.hidden
                whiteSpace = WhiteSpace.nowrap
            }
            +props.mug.name
        }
        div {
            css {
                paddingTop = 5.pct
            }
            +"£${props.mug.price}0"
        }
    }
}

val MugDetailsHover = FC<MugItemGridProps> { props ->

    SweepImageComponent {
        width = 96.pct
        height = 60.pct // ensures a good square for proportions
        marginInline = 3.pct
        srcList = props.mug.getAllPictureSrcs()
        refresh = false
    }
    div {
        css {
            fontNormal()
            paddingBlock = 2.pct
            width = 100.pct
            height = "fit-content".unsafeCast<Height>()
            boxSizing = BoxSizing.borderBox
            textOverflow = TextOverflow.ellipsis
            overflow = Overflow.hidden
            whiteSpace = WhiteSpace.nowrap
        }
        +props.mug.name
    }
    div {
        css {
            fontSmall()
            paddingTop = 2.pct
        }
        +props.mug.description
    }
    div {
        css {
            height = 5.pct
            paddingTop = 5.pct
            display = Display.flex
            flexDirection = FlexDirection.column
            justifyContent = JustifyContent.center
            alignContent = AlignContent.center
            overflow = Overflow.hidden
        }
        IconButton {
            AddShoppingCart()
            div {
                css {
                    fontNormal()
                    marginInline = 2.vw
                }
                +"Add to cart"
            }
            div {
                css {
                    fontNormal()
                }
                +"£${props.mug.price}0"
            }
            onClick = {
                props.onClickAddToCart(props.mug)
            }
        }
    }
}