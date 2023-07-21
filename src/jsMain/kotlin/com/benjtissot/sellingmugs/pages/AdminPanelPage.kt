package com.benjtissot.sellingmugs.pages

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.buttons.LogoutButtonComponent
import com.benjtissot.sellingmugs.components.createProduct.CreateProductComponent
import com.benjtissot.sellingmugs.components.lists.ManageUsersComponent
import com.benjtissot.sellingmugs.entities.local.User
import com.benjtissot.sellingmugs.entities.openAI.CustomStatusCode
import csstype.*
import emotion.react.css
import io.ktor.client.call.*
import io.ktor.http.*
import io.ktor.util.logging.*
import kotlinx.coroutines.launch
import mui.icons.material.PersonOutline
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
            height = 100.pct
            paddingTop = 2.vh
            boxSizing = BoxSizing.borderBox
        }

        if ((props.session.user?.userType ?: Const.UserType.CLIENT) == Const.UserType.ADMIN){

            div {
                css {
                    justifySpaceBetween()
                    flexDirection = FlexDirection.row
                    height = 93.pct
                    width = 100.pct
                }

                CreateProductComponent {
                    navigate = props.navigate
                    session = props.session
                    onCreatingMugs = {subject, artType ->
                        props.setAlert(infoAlert("You are creating mugs on the subject of $subject in a ${artType.type} style", "Mug Creation"))
                    }
                    onMugsCreationResponse = { httpResponse ->
                        when (httpResponse.status) {
                            HttpStatusCode.OK -> {
                                scope.launch {
                                    val statusCodes : List<CustomStatusCode> = httpResponse.body()
                                    statusCodes.forEach { status ->
                                        println(status.print())
                                    }
                                }
                                props.setAlert(successAlert("You have successfully created your mugs"))
                            }
                            Const.HttpStatusCode_OpenAIUnavailable -> props.setAlert(errorAlert("OpenAI is unavailable, please try later"))
                            else -> props.setAlert(errorAlert("There has been a problem during creation. Consult Logs."))
                        }

                    }
                    onCreatingCategories = { nbCat, nbVal, artType ->
                        props.setAlert(infoAlert("You are creating $nbCat categories with $nbVal mugs each. For style, you have chosen ${artType ?: "Automatic"}", "Categories and mugs"))
                    }
                    onCategoriesCreationResponse = { httpResponse ->
                        when (httpResponse.status) {
                            HttpStatusCode.OK -> {
                                scope.launch {
                                    val statusCodes : List<CustomStatusCode> = httpResponse.body()
                                    statusCodes.forEach { status ->
                                        println(status.print())
                                    }
                                }
                                props.setAlert(successAlert("You have successfully created your categories and your mugs"))
                            }
                            Const.HttpStatusCode_OpenAIUnavailable -> props.setAlert(errorAlert("OpenAI is unavailable, please try later"))
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
                    alignItems = AlignItems.center
                    width = 100.pct
                    maxHeight = 5.vh
                    paddingBlock = 2.vh
                    paddingInline = 5.vw
                    boxSizing = BoxSizing.borderBox
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