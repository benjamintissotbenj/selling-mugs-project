package com.benjtissot.sellingmugs.components

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.highLevel.PopupHeaderComponent
import com.benjtissot.sellingmugs.components.forms.CreateProductForm
import com.benjtissot.sellingmugs.components.lists.MugListComponent
import com.benjtissot.sellingmugs.components.popups.MugDetailsPopup
import com.benjtissot.sellingmugs.entities.Mug
import com.benjtissot.sellingmugs.entities.User
import com.benjtissot.sellingmugs.entities.printify.ImageForUpload
import com.benjtissot.sellingmugs.entities.printify.ImageForUploadReceive
import com.benjtissot.sellingmugs.entities.printify.MugProductInfo
import com.benjtissot.sellingmugs.pages.selectBase64ContentFromURLData
import csstype.*
import emotion.react.css
import io.ktor.client.call.*
import io.ktor.http.*
import io.ktor.util.logging.*
import kotlinx.coroutines.launch
import mui.material.Button
import org.w3c.dom.HTMLDivElement
import org.w3c.files.FileReader
import react.FC
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.img
import react.router.useNavigate
import react.useEffectOnce
import react.useState


private val LOG = KtorSimpleLogger("UserInfoComponent.kt")

external interface UserInfoProps : NavigationProps {
    var user: User
}

val UserInfoComponent = FC<UserInfoProps> { props ->

    var numberOfOrders by useState(0)
    var customMugsList : List<Mug> by useState(emptyList())
    var popupTarget : HTMLDivElement? by useState(null)
    var mugShowDetails : Mug? by useState(null)

    useEffectOnce {
        scope.launch {
            numberOfOrders = getUserOrderCount(props.user.id)
            customMugsList = getUserCustomMugList(props.user.id)
        }
    }
    div {
        css {
            display = Display.flex
            flexDirection = FlexDirection.column
            width = 100.pct
            marginBottom = 5.vh
        }

        div {
            css {
                display = Display.flex
                flexDirection = FlexDirection.row
                width = 100.pct
            }
            div {
                css {
                    fontNormalPlus()
                    display = Display.flex
                    flexDirection = FlexDirection.column
                    width = 50.pct
                    padding = 2.vw
                    boxSizing = BoxSizing.borderBox
                }
                div {
                    +"Name : ${props.user.firstName} ${props.user.lastName.uppercase()}"
                }
                div {
                    +"Email : ${props.user.email}"
                }
                div {
                    +"Number of orders : $numberOfOrders"
                }
            }

            div {
                css {
                    width = 50.pct
                    padding = 2.vw
                    boxSizing = BoxSizing.borderBox
                }
                +"Address information coming soon"
            }
        }

        // Display a list of the custom mugs made by the user
        MugListComponent {
            list = customMugsList
            title = "Your custom mugs"
            onMouseEnterItem = { mug, target ->
                mugShowDetails = mug
                popupTarget = target
            }
        }
    }


    // Declare popup top level
    MugDetailsPopup {
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
}
