package com.benjtissot.sellingmugs.components.lists

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.createProduct.HoverImageComponent
import com.benjtissot.sellingmugs.components.forms.SearchBarComponent
import com.benjtissot.sellingmugs.entities.local.Category
import com.benjtissot.sellingmugs.entities.local.Mug
import csstype.*
import emotion.react.css
import kotlinx.js.jso
import mui.icons.material.ExpandMore
import mui.icons.material.Search
import mui.material.*
import mui.system.PropsWithSx
import mui.system.sx
import org.w3c.dom.HTMLDivElement
import react.*
import react.dom.html.InputType
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.header
import react.dom.onChange
import ringui.Col
import ringui.Grid
import ringui.Row


external interface MugListProps: Props {
    var displayStyle: String
    var availableCategories: List<Category>?
    var selectedCategories: List<Category>
    var onChangeSelectedCategories: (List<String>) -> Unit
    var orderBy: Const.OrderBy
    var searchString: String
    var onChangeOrderBy: (Const.OrderBy) -> Unit
    var onClickCustomItem: () -> Unit
    var onClickMore: () -> Unit
    var onSearch: (String) -> Unit
    var list: List<Mug>
    var title: String?
    var onMouseEnterItem: (Mug, HTMLDivElement) -> Unit
    var onClickAddToCart: (Mug) -> Unit
    var totalNumberOfMugs: Int
    var onClickItem: (Mug) -> Unit
}

val MugListComponent = FC<MugListProps> {
        props ->

    props.title?.let {
        header {
            css {
                width = 100.pct
            }
            div {
                css {
                    fontBig()
                    marginLeft = 5.pct
                    width = 35.pct
                }
                +it
            }
            props.availableCategories?.let {
                div {
                    css {
                        width = "fit-content".unsafeCast<Width>()
                        display = Display.flex
                        flexDirection = FlexDirection.row
                        justifyContent = JustifyContent.spaceBetween
                        alignItems = AlignItems.center
                        marginRight = 5.pct
                    }
                    SearchBarComponent {
                        searchString = props.searchString
                        onSubmit = { searchString ->
                            props.onSearch(searchString)
                        }
                    }
                }
            }
        }
    }

    props.availableCategories?.let {
        div {
            css {
                display = Display.flex
                flexDirection = FlexDirection.row
                justifyContent = JustifyContent.spaceBetween
                alignItems = AlignItems.center
                paddingBlock = 2.vh
                height = 3.vh
                width = 100.pct
                color = Color(Const.ColorCode.BLACK.code())
            }
            div {
                css {
                    fontNormal()
                    marginLeft = 5.pct
                    width = 35.pct
                }
                +"Modifiers :"
            }
            // Order By
            div {
                css {
                    display = Display.flex
                    flexDirection = FlexDirection.rowReverse
                    alignItems = AlignItems.center
                    width = 25.pct
                }
                Select {
                    // Attributes
                    css {
                        width = 50.pct
                        minWidth = 50.pct
                        height = 3.rem
                        maxHeight = 5.vh
                        minHeight = 40.px
                        fontNormal()
                        marginRight = 1.vw
                    }

                    value = props.orderBy.value
                    onChange = { event, _ ->
                        props.onChangeOrderBy(Const.OrderBy.valueOf(event.target.value))
                    }

                    // Children, in the selector
                    Const.OrderBy.values().forEach { orderByVal ->
                        MenuItem {
                            value = orderByVal.value
                            +orderByVal.cleanName()
                        }
                    }
                }
                div {
                    css {
                        fontNormal()
                        marginInline = 1.vw
                    }
                    +"Order by:"
                }
            }

            // Filter by category
            props.availableCategories?.let {
                div {
                    css {
                        display = Display.flex
                        flexDirection = FlexDirection.rowReverse
                        alignItems = AlignItems.center
                        width = 40.pct
                    }
                    Select {
                        // Attributes
                        css {
                            width = 70.pct
                            minWidth = 70.pct
                            height = 3.rem
                            maxHeight = 5.vh
                            minHeight = 40.px
                            fontNormal()
                            marginRight = 4.vw
                        }


                        multiple = true
                        value = props.selectedCategories.map { cat -> cat.id }.toTypedArray()
                        onChange = { event, _ ->
                            val tempCategoriesId = event.target.value.unsafeCast<Array<String>>()
                            val tempCategoriesIdList: ArrayList<String> = arrayListOf()
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
                    div {
                        css {
                            fontNormal()
                            marginInline = 1.vw
                        }
                        +"Filter by:"
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
                                    onClickItem = {mug -> props.onClickItem(mug)}
                                }
                            } ?: div {
                                css {
                                    display = Display.flex
                                    flexDirection = FlexDirection.column
                                    alignContent = AlignContent.center
                                    width = 90.pct
                                    height = (1.5 * this.width.unsafeCast<Percentage>()).unsafeCast<Height>()
                                    margin = 5.pct
                                    marginTop = 2.pct
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
