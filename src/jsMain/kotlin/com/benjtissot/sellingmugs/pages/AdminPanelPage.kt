package com.benjtissot.sellingmugs.pages

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.createProduct.CreateProductComponent
import com.benjtissot.sellingmugs.components.buttons.LogoutButtonComponent
import com.benjtissot.sellingmugs.components.lists.ManageUsersComponent
import com.benjtissot.sellingmugs.components.lists.UserItem
import com.benjtissot.sellingmugs.entities.User
import emotion.react.css
import io.ktor.util.logging.*
import kotlinx.coroutines.launch
import mui.icons.material.AddCircle
import mui.icons.material.Person
import mui.material.IconButton
import react.FC
import react.dom.html.ReactHTML.div
import react.router.useNavigate
import react.useEffect
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
    var productPopupOpen by useState(false)
    var usersPopupOpen by useState(false)

    var userList by useState(emptyList<User>())

    div {
        css {
            mainPageDiv()
            contentCenteredHorizontally()
        }

        if ((props.session.user?.userType ?: Const.UserType.CLIENT) == Const.UserType.ADMIN){
            div {
                css {
                    fontNormal()
                }
                +"Hello Admin Page"
                +"Extra message $message"
            }

            if (!productPopupOpen){
                IconButton {
                    div {
                        +"Create Product"
                    }
                    AddCircle()
                    onClick = {
                        productPopupOpen = true
                    }
                }
            } else {
                CreateProductComponent{
                    onProductCreatedSuccess = { productId ->
                        LOG.debug("Created product $productId")
                    }
                    onProductCreatedFailed = { productId ->
                        // TODO : Error Message
                        LOG.debug("Could not create product")
                    }
                    onClickClose = {
                        productPopupOpen = false
                    }
                }
            }

            if (!usersPopupOpen){
                IconButton {
                    div {
                        +"Manage users"
                    }
                    Person()
                    onClick = {
                        usersPopupOpen = true
                    }
                }
            } else {
                useEffect {

                }
                ManageUsersComponent {
                    this.userList = userList
                    onChangeUserType = { user ->

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
}

fun selectBase64ContentFromURLData(input : String) : String {
    val list = input.split("base64,")
    var base64Content = ""
    for (i in 1 until list.size){
        base64Content += list[i]
    }
    return base64Content
}