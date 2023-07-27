package com.benjtissot.sellingmugs.pages

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.lists.MugDetailsComplete
import com.benjtissot.sellingmugs.components.lists.MugDetailsHover
import com.benjtissot.sellingmugs.components.lists.MugItemGridComponent
import com.benjtissot.sellingmugs.entities.local.Mug
import csstype.AlignItems
import csstype.Display
import csstype.FlexDirection
import csstype.JustifyContent
import emotion.react.css
import io.ktor.util.logging.*
import kotlinx.coroutines.launch
import react.FC
import react.dom.html.ReactHTML.div
import react.router.useParams
import react.useEffectOnce
import react.useState

private val LOG = KtorSimpleLogger("ProductInfoPage.kt")


val ProductInfoPage = FC<NavigationProps> { props ->

    val mugPrintifyId = useParams()[Const.mugPrintifyId] ?: ""
    var mug : Mug? by useState(null)

    useEffectOnce {
        scope.launch {
            mug = getMugByPrintifyId(mugPrintifyId)
        }
    }
    mug?.let {
        MugDetailsComplete {
            this.mug = it
            this.onClickAddToCart = {
                onClickAddToCart(it, props.setAlert, props.session)
            }
        }
    } ?: let {
        div {
            css {
                display = Display.flex
                flexDirection = FlexDirection.column
                alignItems = AlignItems.center
                justifyContent = JustifyContent.center
            }
            +"Data is loading, please wait..."
        }
    }
}
