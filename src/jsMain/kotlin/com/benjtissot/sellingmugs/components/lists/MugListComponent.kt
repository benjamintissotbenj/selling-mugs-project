package com.benjtissot.sellingmugs.components.lists

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.createProduct.HoverImageComponent
import com.benjtissot.sellingmugs.entities.Mug
import csstype.*
import emotion.react.css
import org.w3c.dom.HTMLDivElement
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.header
import ringui.*


external interface MugListProps: Props {
    var displayStyle: String
    var onClickCustomItem: () -> Unit
    var list: List<Mug>
    var title: String?
    var onMouseEnterItem: (Mug, HTMLDivElement) -> Unit
    var onClickAddToCart: (Mug) -> Unit
}

val MugListComponent = FC<MugListProps> {
        props ->

    props.title?.let{
        header {
            css {
                width = 100.pct
            }
            div {
                css {
                    fontBig()
                    marginLeft = 10.vw
                }
                +it
            }
        }
    }

    if (props.displayStyle == Const.mugListDisplayList){
        div {
            css {
                display = Display.flex
                overflowX = "auto".unsafeCast<Overflow>()
                scrollBehavior = ScrollBehavior.smooth
                paddingBlock = 1.rem
            }
            props.list.forEach { mugItm ->
                MugItemListComponent {
                    mug = mugItm
                    this.onMouseEnterItem = props.onMouseEnterItem
                }
            }
        }
    } else if (props.displayStyle == Const.mugListDisplayGrid){
        // Create a chunked list to display the mugs in a list
        val mugArrayList : ArrayList<Mug?> = arrayListOf(null)
        mugArrayList.addAll(props.list)
        div {
            css {
                width = 100.pct
                display = Display.flex
                flexDirection = FlexDirection.column
                alignItems = AlignItems.center
            }
            Grid {
                css {
                    width = 100.pct
                    height = "fit-content".unsafeCast<Height>()
                    padding = 0.px
                    margin = 16.px
                    boxSizing = BoxSizing.borderBox
                    overflowY = "auto".unsafeCast<Overflow>()
                }

                    Row {
                        css {
                            padding = 0.px
                            marginInline = 3.pct
                            boxSizing = BoxSizing.borderBox
                        }

                        mugArrayList.forEach { mugItm ->
                            Col {
                                css {
                                    colDefault()
                                    contentCenteredHorizontally()
                                    boxSizing = BoxSizing.borderBox
                                }
                                // Different widths for different screen sizes
                                xs = 12
                                sm = 6
                                md = 4
                                lg = 3
                                mugItm?.let {
                                    MugItemGridComponent {
                                        mug = mugItm
                                        onClickAddToCart = { mug -> props.onClickAddToCart(mug)}
                                    }
                                } ?: div {
                                    css {
                                        display = Display.flex
                                        flexDirection = FlexDirection.column
                                        alignContent = AlignContent.center
                                        width = 90.pct
                                        height = (1.5* this.width as Percentage).unsafeCast<Height>()
                                        margin = 5.pct
                                        paddingTop = 5.pct
                                        boxSizing = BoxSizing.borderBox
                                    }

                                    HoverImageComponent {
                                        width = 90.pct
                                        height = 74.pct
                                        srcMain = "https://images.printify.com/api/catalog/5e440fbfd897db313b1987d1.jpg?s=320"
                                        srcHover = "https://images.printify.com/api/catalog/6358ee8d99b22ccab005e8a7.jpg?s=320"
                                        onClick = {
                                            props.onClickCustomItem()
                                        }
                                    }


                                    div {
                                        css {
                                            padding = 5.pct
                                        }
                                        +"Customize your own mug !"
                                    }
                                }
                            }
                        }
                    }

            }
        }
    }
}