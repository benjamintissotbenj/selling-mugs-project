package com.benjtissot.sellingmugs.pages

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.buttons.LogoutButtonComponent
import com.benjtissot.sellingmugs.components.createProduct.CreateProductComponent
import com.benjtissot.sellingmugs.components.lists.ManageUsersComponent
import com.benjtissot.sellingmugs.entities.User
import emotion.react.css
import io.ktor.util.logging.*
import kotlinx.coroutines.launch
import mui.icons.material.AddCircle
import mui.icons.material.Person
import mui.icons.material.PersonOutline
import mui.material.*
import react.FC
import react.dom.html.ReactHTML.div
import react.router.useNavigate
import react.useEffectOnce
import react.useState

private val LOG = KtorSimpleLogger("AdminPanelPage.kt")

val AdminPanelPage = FC<NavigationProps> { props ->

    var message by useState("")
    useEffectOnce {
        scope.launch {
            message = getUserInfo()
        }
    }
    var productPopupOpen by useState(false)
    var usersPopupOpen by useState(false)

    var userList by useState(emptyList<User>())

    useEffectOnce {
        scope.launch {
            userList = getUserList()
        }
    }

    div {
        css {
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

            IconButton {
                div {
                    +"Open User Info"
                }
                PersonOutline()
                onClick = {
                    props.navigate.invoke(USER_INFO_PATH)
                }
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
                    setAlert = props.setAlert
                    onProductCreatedSuccess = { productId, productName ->
                        setAlert(successAlert( "Product $productName created successfully !"))
                        LOG.debug("Created product $productId")
                    }
                    onProductCreatedFailed = { productId ->
                        setAlert(errorAlert( "Could not create product"))
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
                ManageUsersComponent {
                    this.userList = userList
                    onChangeUserType = { user ->
                        scope.launch {
                            updateUser(user)
                            userList = getUserList()
                        }
                    }
                    onDeleteUser = { userId ->
                        scope.launch {
                            deleteUser(userId)
                            userList = getUserList()
                        }
                    }

                }

            }

            LogoutButtonComponent {
                session = props.session
                updateSession = props.updateSession
                navigate = props.navigate
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