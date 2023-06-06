package com.benjtissot.sellingmugs.pages

import ImageDrop
import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.CreateProductComponent
import com.benjtissot.sellingmugs.components.LogoutButtonComponent
import com.benjtissot.sellingmugs.entities.printify.ImageForUpload
import emotion.react.css
import io.ktor.util.logging.*
import kotlinx.coroutines.launch
import mui.icons.material.AddCircle
import mui.material.IconButton
import org.w3c.dom.asList
import org.w3c.files.File
import org.w3c.files.FileReader
import react.FC
import react.dom.html.InputType
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.form
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.label
import react.router.useNavigate
import react.useEffectOnce
import react.useState

private val LOG = KtorSimpleLogger("AdminPanelPage.kt")

external interface AdminPanelPageProps : SessionPageProps {
}

val AdminPanelPage = FC<SessionPageProps> { props ->

    val navigateAdmin = useNavigate()
    var message by useState("")
    useEffectOnce {
        scope.launch {
            message = getUserInfo()
        }
    }
    var createProduct by useState(false)
    var productToPublishId : String by useState("")

    if ((props.session.user?.userType ?: Const.UserType.CLIENT) == Const.UserType.ADMIN){
        div {
            css {
                mainPageDiv()
                contentCenteredHorizontally()
            }

            div {
                css {
                    fontNormal()
                }
                +"Hello Admin Page"
                +"Extra message $message"
            }

            if (!createProduct){
                IconButton {
                    div {
                        +"Create Product"
                    }
                    AddCircle()
                    onClick = {
                        createProduct = true
                    }
                }
            } else {
                CreateProductComponent{
                    onProductCreatedSuccess = { productId ->
                        productToPublishId = productId
                        LOG.debug("Created product $productId")
                    }
                    onProductCreatedFailed = { productId ->
                        // TODO : Error Message
                        LOG.debug("Could not create product")
                    }
                }

                if (productToPublishId.isNotBlank()){
                    IconButton {

                    }
                }
            }


        }

        LogoutButtonComponent {
            session = props.session
            updateSession = props.updateSession
            navigate = navigateAdmin
        }

    } else {
        div {
            divDefaultCss()
            +"You must be an admin to view this page"
        }
    }
}

fun selectBase64ContentFromURLData(input : String) : String {
    val list = input.split("base64,")
    var base64Content = ""
    for (i in 1 until list.size){
        base64Content += list[i]
    }
    return base64Content
}