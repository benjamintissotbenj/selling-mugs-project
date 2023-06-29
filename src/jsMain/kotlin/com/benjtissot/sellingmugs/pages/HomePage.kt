package com.benjtissot.sellingmugs.pages

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.createProduct.HoverImageComponent
import com.benjtissot.sellingmugs.components.lists.MugListComponent
import com.benjtissot.sellingmugs.entities.Mug
import csstype.*
import emotion.react.css
import kotlinx.coroutines.launch
import react.FC
import react.dom.html.ReactHTML.div
import react.useEffectOnce
import react.useState


var checkRedirect: String? = null

val Homepage = FC<NavigationProps> { props ->
    val navigateFun = props.navigate
    var mugList by useState(emptyList<Mug>())

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

    div {
        MugListComponent {
            list = mugList
            title = "Best for you"
            onItemClick = { mug ->
                scope.launch {
                    // Adding the mug to the cart
                    // TODO : redirect towards mug page or open a popup or something
                    addMugToCart(mug)
                    mugList = getMugList() // updates client
                }
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
                    props.navigate.invoke(CUSTOM_MUG_PATH)
                }
            }
        }

    }

}