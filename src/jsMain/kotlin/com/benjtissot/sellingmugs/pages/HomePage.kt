package com.benjtissot.sellingmugs.pages

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.lists.MugListComponent
import com.benjtissot.sellingmugs.components.popups.MugDetailsPopup
import com.benjtissot.sellingmugs.entities.local.Category
import com.benjtissot.sellingmugs.entities.local.Mug
import com.benjtissot.sellingmugs.entities.local.Session
import csstype.*
import emotion.react.css
import kotlinx.coroutines.launch
import mui.icons.material.ExpandMore
import mui.material.IconButton
import org.w3c.dom.HTMLDivElement
import react.FC
import react.dom.html.ReactHTML.div
import react.useEffectOnce
import react.useState


var checkRedirect: String? = null

val Homepage = FC<NavigationProps> { props ->
    val navigateFun = props.navigate
    var availableCategories by useState(emptyList<Category>())
    var selectedCategories by useState(emptyList<Category>())
    var mugList by useState(emptyList<Mug>())
    var currentPage by useState(0)
    var totalNumberOfMugs by useState(0)

    var popupTarget : HTMLDivElement? by useState(null)
    var mugShowDetails : Mug? by useState(null)
    var orderBy by useState(Const.OrderBy.VIEWS)


    // At first initialisation, get the list
    // Alternative is useState when we want to persist something across re-renders
    useEffectOnce {
        scope.launch {
            checkRedirect = checkRedirect()
            val checkRedirectPaths = checkRedirect?.split("/") ?: emptyList()
            if (checkRedirect?.isNotEmpty() == true && checkRedirectPaths.isNotEmpty() && checkRedirectPaths.map { ALL_FRONT_END_PATHS.contains("/$it") }.contains(true)) {
                navigateFun.invoke(checkRedirect?:"")
                checkRedirect = ""
            } else {
                availableCategories = getAllCategories()
                mugList = getMugList(selectedCategories, currentPage, orderBy = orderBy)
                totalNumberOfMugs = getTotalMugCount(selectedCategories)
            }
        }
    }

    // Declare popup top level
    MugDetailsPopup {
        this.marginTop = -11
        this.marginBottom = -11
        this.popupTarget = popupTarget
        this.onMouseLeavePopup = {
            mugShowDetails = null
            popupTarget = null
        }
        this.mug = mugShowDetails
        this.onClickAddToCart = { mug ->
            onClickAddToCart(mug, props.setAlert, props.session)
        }
    }

    div {
        css {
            width = 100.pct
            height = 100.pct
            display = Display.flex
            flexDirection = FlexDirection.column
            alignItems = AlignItems.center
        }
        MugListComponent {
            displayStyle = Const.mugListDisplayGrid
            list = mugList
            this.availableCategories = availableCategories
            this.selectedCategories = selectedCategories
            title = "Best for you"
            this.totalNumberOfMugs = totalNumberOfMugs
            onClickCustomItem = {
                scope.launch{
                    recordClick(props.session.clickDataId, Const.ClickType.CUSTOM_MUG_OPEN_PAGE.type)
                }
                props.navigate.invoke(CUSTOM_MUG_PATH)
            }
            onClickMore = {
                val tempMugList = ArrayList(mugList)
                val tempCurrentPage = currentPage + 1
                scope.launch {
                    // update the mugList incrementally so that the UI doesn't have to wait for all the mugs at once
                    tempMugList.addAll(getMugList(selectedCategories, tempCurrentPage, orderBy = orderBy))
                    mugList = tempMugList
                }
                currentPage = tempCurrentPage
            }
            this.orderBy = orderBy
            onChangeOrderBy = { orderByTemp ->
                scope.launch {
                    mugList = getMugList(selectedCategories, currentPage, orderBy = orderByTemp)
                }
                orderBy = orderByTemp
            }
            onChangeSelectedCategories = { categoryIds ->
                println("Selected Categories:")
                categoryIds.forEach {
                    println("$it,")
                }
                scope.launch {
                    val tempSelectedCategories = if (categoryIds.isNotEmpty()) { getCategoriesByIds(categoryIds) } else { emptyList<Category>() }
                    selectedCategories = tempSelectedCategories
                    totalNumberOfMugs = getTotalMugCount(tempSelectedCategories)
                    // When changing categories, make sure that you stay on the same page (i.e. if you asked for more mugs to be shown)
                    val tempMugList = ArrayList(emptyList<Mug>())
                    for (i : Int in 0..currentPage){
                        // update the mugList incrementally so that the UI doesn't have to wait for all the mugs at once
                        tempMugList.addAll(getMugList(tempSelectedCategories, currentPage, orderBy = orderBy))
                        mugList = tempMugList
                    }
                }
            }
            onMouseEnterItem = { mug, target ->
                mugShowDetails = mug
                popupTarget = target
            }
            onClickAddToCart = { mug ->
                onClickAddToCart(mug, props.setAlert, props.session)
            }
            onClickItem = { mug ->
                scope.launch {
                    increaseMugViews(mug.printifyId)
                }
                props.navigate.invoke("$PRODUCT_INFO_PATH/${mug.printifyId}")
            }
        }
    }
}

fun onClickAddToCart(mug: Mug?, setAlert: (AlertState) -> Unit, session: Session) {
    // Add product to cart
    scope.launch {
        mug?.let {
            addMugToCart(it)
            setAlert(successAlert("Mug added to card !"))
        } ?: setAlert(errorAlert())
        recordClick(session.clickDataId, Const.ClickType.ADD_MUG_TO_CART.type)
    }
}