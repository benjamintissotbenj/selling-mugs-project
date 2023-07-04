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
import kotlinx.js.timers.Timeout
import kotlinx.js.timers.clearInterval
import kotlinx.js.timers.setInterval
import org.w3c.dom.HTMLDivElement
import react.FC
import react.dom.html.ReactHTML.div
import react.useEffect
import react.useEffectOnce
import react.useState


var checkRedirect: String? = null

val Homepage = FC<NavigationProps> { props ->
    val navigateFun = props.navigate
    var mugList by useState(emptyList<Mug>())


    var closePopupTimeout: Timeout? = null
    var leftList by useState(true)
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
/*
    useEffect {
        if (popupTarget != null) {
            if (closePopupTimeout == null) {
                closePopupTimeout = setInterval({
                    if (leftList){
                        closePopupTimeout?.let {clearInterval(it)}
                        popupTarget = null
                        mugShowDetails = null
                    }
                }, 1000)
            }
        } else {
            closePopupTimeout?.let { clearInterval(it) }
        }
    }*/

    // Declare popup top level
    MugDetailsPopup {
        this.popupTarget = popupTarget
        this.onMouseLeavePopup = {
            mugShowDetails = null
            popupTarget = null
        }
        this.mug = mugShowDetails
        this.onClickAddToCart = { mug ->
            scope.launch {
                recordClick(props.session.clickDataId, Const.ClickType.ADD_MUG_TO_CART.type)
            }
            // Add product to cart
            scope.launch {
                mug?.let {
                    addMugToCart(it)
                    props.setAlert(successAlert("Mug added to card !"))
                } ?: let {
                    props.setAlert(errorAlert())
                }
            }
        }
    }

    div {
        MugListComponent {
            list = mugList
            title = "Best for you"
            onMouseEnterItem = { mug, target ->
                leftList = false
                mugShowDetails = mug
                popupTarget = target
            }
            onMouseLeaveList = {
                leftList = true
            }
        }

        div {
            css {
                padding = 5.vw
                contentCenteredHorizontally()
            }
            +"Customize your own mug !"

            HoverImageComponent {
                width = 10.rem
                height = 10.rem
                srcMain = "https://images.printify.com/api/catalog/5e440fbfd897db313b1987d1.jpg?s=320"
                srcHover = "https://images.printify.com/api/catalog/6358ee8d99b22ccab005e8a7.jpg?s=320"
                onClick = {
                    scope.launch {
                        recordClick(props.session.clickDataId, Const.ClickType.CUSTOMISED_MUG.type)
                    }
                    props.navigate.invoke(CUSTOM_MUG_PATH)
                }
            }
        }

    }

}