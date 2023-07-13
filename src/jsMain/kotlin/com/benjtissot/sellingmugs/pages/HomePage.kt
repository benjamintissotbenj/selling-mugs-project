package com.benjtissot.sellingmugs.pages

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.createProduct.HoverImageComponent
import com.benjtissot.sellingmugs.components.lists.MugListComponent
import com.benjtissot.sellingmugs.components.popups.MugDetailsPopup
import com.benjtissot.sellingmugs.entities.Mug
import csstype.rem
import csstype.vw
import emotion.react.css
import kotlinx.coroutines.launch
import org.w3c.dom.HTMLDivElement
import react.FC
import react.dom.html.ReactHTML.div
import react.useEffectOnce
import react.useState


var checkRedirect: String? = null

val Homepage = FC<NavigationProps> { props ->
    val navigateFun = props.navigate
    var mugList by useState(emptyList<Mug>())

    var popupTarget : HTMLDivElement? by useState(null)
    var mugShowDetails : Mug? by useState(null)


    // At first initialisation, get the list
    // Alternative is useState when we want to persist something across re-renders
    useEffectOnce {
        scope.launch {
            checkRedirect = checkRedirect()
            if (checkRedirect!= null && ALL_FRONT_END_PATHS.contains(checkRedirect)) {
                navigateFun.invoke(checkRedirect?:"")
                checkRedirect = ""
            } else {
                mugList = getMugList()
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
            // Add product to cart
            scope.launch {
                mug?.let {
                    addMugToCart(it)
                    props.setAlert(successAlert("Mug added to card !"))
                } ?: let {
                    props.setAlert(errorAlert())
                }
                recordClick(props.session.clickDataId, Const.ClickType.ADD_MUG_TO_CART.type)
            }
        }
    }

    div {
        // TODO: improve the muglist component, integrate the Customizable mug better
        MugListComponent {
            onClickCustomItem = {
                scope.launch{
                    recordClick(props.session.clickDataId, Const.ClickType.CUSTOM_MUG_OPEN_PAGE.type)
                }
                props.navigate.invoke(CUSTOM_MUG_PATH)
            }
            displayStyle = Const.mugListDisplayGrid
            list = mugList
            title = "Best for you"
            onMouseEnterItem = { mug, target ->
                mugShowDetails = mug
                popupTarget = target
            }
        }


    }

}