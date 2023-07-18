package com.benjtissot.sellingmugs.pages

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.buttons.LogoutButtonComponent
import com.benjtissot.sellingmugs.components.createProduct.CreateProductComponent
import com.benjtissot.sellingmugs.components.lists.ManageUsersComponent
import com.benjtissot.sellingmugs.entities.User
import csstype.*
import emotion.react.css
import io.ktor.http.*
import io.ktor.util.logging.*
import kotlinx.coroutines.launch
import mui.icons.material.PersonOutline
import mui.icons.material.Send
import mui.material.IconButton
import react.FC
import react.dom.html.ReactHTML.div
import react.useEffectOnce
import react.useState

private val LOG = KtorSimpleLogger("AdminPanelPage.kt")

val AdminPanelPage = FC<NavigationProps> { props ->

    useEffectOnce {
        scope.launch {
        }
    }
    var productPopupOpen by useState(false)

    var userList by useState(emptyList<User>())

    useEffectOnce {
        scope.launch {
            userList = getUserList()
        }
    }

    div {
        css {
            contentCenteredHorizontally()
            width = 100.pct
            height = 95.pct
            paddingTop = 2.vh
            boxSizing = BoxSizing.borderBox
        }

        if ((props.session.user?.userType ?: Const.UserType.CLIENT) == Const.UserType.ADMIN){

            div {
                css {
                    justifySpaceBetween()
                    flexDirection = FlexDirection.row
                    height = 95.pct
                    width = 100.pct
                }

                CreateProductComponent {
                    navigate = props.navigate
                    onCreatingMugs = {subject, artType ->
                        props.setAlert(infoAlert("You are creating mugs on the subject of $subject in a ${artType.type} style", "Mug Creation"))
                    }
                    onMugsCreationResponse = { httpStatusCode ->
                        when (httpStatusCode) {
                            HttpStatusCode.OK -> props.setAlert(successAlert("You have successfully created your mugs"))
                            else -> props.setAlert(errorAlert("There has been a problem during creation. Consult Logs."))
                        }

                    }
                }

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
            div {
                css {
                    justifySpaceBetween()
                    width = 100.pct
                    boxSizing = BoxSizing.borderBox
                    paddingLeft = 5.vw
                    paddingRight = 5.vw
                    flexDirection = FlexDirection.row
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

                LogoutButtonComponent {
                    session = props.session
                    updateSession = props.updateSession
                    navigate = props.navigate
                }
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