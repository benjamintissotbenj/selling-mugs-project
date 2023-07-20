package com.benjtissot.sellingmugs.components.lists

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.createProduct.HoverImageComponent
import com.benjtissot.sellingmugs.entities.local.Category
import com.benjtissot.sellingmugs.entities.local.Mug
import csstype.*
import emotion.react.css
import mui.icons.material.ExpandMore
import mui.material.IconButton
import mui.material.MenuItem
import mui.material.Select
import mui.material.Slider
import mui.system.sx
import org.w3c.dom.HTMLDivElement
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.header
import react.useState
import ringui.Col
import ringui.Grid
import ringui.Row


external interface MugListProps: Props {
    var displayStyle: String
    var availableCategories: List<Category>?
    var selectedCategories: List<Category>
    var onChangeSelectedCategories: (List<String>) -> Unit
    var onClickCustomItem: () -> Unit
    var onClickMore: () -> Unit
    var list: List<Mug>
    var title: String?
    var onMouseEnterItem: (Mug, HTMLDivElement) -> Unit
    var onClickAddToCart: (Mug) -> Unit
    var totalNumberOfMugs: Int
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

            // Filter by category
            props.availableCategories?.let {
                Select {
                    // Attributes
                    css {
                        width = 100.rem
                        maxWidth = 20.pct
                        minWidth = 110.px
                        height = 3.rem
                        maxHeight = 5.vh
                        minHeight = 40.px
                        color = NamedColor.white
                        fontNormal()
                        marginRight = 4.vw
                    }

                    multiple = true
                    value = props.selectedCategories.map { cat -> cat.id }.toTypedArray()
                    onChange = { event, _ ->
                        val tempCategoriesId = event.target.value.unsafeCast<Array<String>>()
                        val tempCategoriesIdList : ArrayList<String> = arrayListOf()
                        tempCategoriesIdList.addAll(tempCategoriesId)
                        props.onChangeSelectedCategories(tempCategoriesIdList)

                    }


                    // Children, in the selector
                    props.availableCategories?.forEach { category ->
                        MenuItem {
                            value = category.id
                            +category.name
                        }
                    }
                }
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
                height = 100.pct
                boxSizing = BoxSizing.borderBox
                display = Display.flex
                flexDirection = FlexDirection.column
                alignItems = AlignItems.center
                overflowY = "auto".unsafeCast<Overflow>()
            }
            Grid {
                css {
                    width = 100.pct
                    height = "fit-content".unsafeCast<Height>()
                    padding = 0.px
                    boxSizing = BoxSizing.borderBox
                }

                Row {
                    css {
                        padding = 0.px
                        marginInline = 3.pct
                        boxSizing = BoxSizing.borderBox
                        height = 65.vh
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
                                    height = (1.5 * this.width.unsafeCast<Percentage>()).unsafeCast<Height>()
                                    margin = 5.pct
                                    boxSizing = BoxSizing.borderBox
                                }

                                HoverImageComponent {
                                    width = 100.pct
                                    height = 100.pct
                                    srcMain = "https://images.printify.com/api/catalog/5e440fbfd897db313b1987d1.jpg?s=320"
                                    srcHover = "https://images.printify.com/api/catalog/6358ee8d99b22ccab005e8a7.jpg?s=320"
                                    onClick = {
                                        props.onClickCustomItem()
                                    }
                                }


                                div {
                                    css {
                                        paddingTop = 5.pct
                                    }
                                    +"Customize your own mug !"
                                }
                                div {
                                    css {
                                        paddingTop = 5.pct
                                    }
                                    +"£7.20"
                                }
                            }
                        }
                    }

                    if (props.list.size < props.totalNumberOfMugs){
                        Col {
                            css {
                                colDefault()
                                contentCenteredHorizontally()
                                boxSizing = BoxSizing.borderBox
                            }
                            // Different widths for different screen sizes
                            xs = 12
                            sm = 12
                            md = 12
                            lg = 12

                            IconButton {
                                ExpandMore {
                                    css { marginInline = 2.vw }
                                }
                                +"Show more"
                                onClick = {
                                    props.onClickMore()
                                }
                            }
                        }
                    }

                }

            }
        }
    }
}