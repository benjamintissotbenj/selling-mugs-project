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
import io.ktor.http.*
import io.ktor.util.logging.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import react.FC
import react.dom.html.ReactHTML.div
import react.router.useParams
import react.useEffectOnce
import react.useState

private val LOG = KtorSimpleLogger("ProductInfoPage.kt")


val ProductInfoPage = FC<NavigationProps> { props ->

    val urlHandle = useParams()[Const.mugShortUrlHandle] ?: ""
    var mug : Mug? by useState(null)

    useEffectOnce {
        scope.launch {
            mug = getMugByURLHandle(urlHandle)
        }
    }
    mug?.let {
        MugDetailsComplete {
            this.showDelete = props.session.user?.userType == Const.UserType.ADMIN
            this.mug = it
            this.onClickAddToCart = {
                scope.launch {
                    onClickAddToCart(it, props.setAlert, props.session)
                    delay(50L)
                    props.updateSession()
                }
            }
            this.onDeleteMug = { httpResponse ->
                when (httpResponse.status) {
                    HttpStatusCode.OK -> {
                        props.setAlert(successAlert("You have deleted the mug successfully", "Deleting mug"))
                        props.navigate.invoke(HOMEPAGE_PATH)
                    }
                    else -> props.setAlert(errorAlert("Something went wrong", "Deleting mug"))
                }
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
