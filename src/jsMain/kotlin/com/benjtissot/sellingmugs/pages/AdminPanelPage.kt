package com.benjtissot.sellingmugs.pages

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.buttons.LogoutButtonComponent
import com.benjtissot.sellingmugs.components.createProduct.CreateProductComponent
import com.benjtissot.sellingmugs.components.createProduct.DisplayCategoriesGenerationResultComponent
import com.benjtissot.sellingmugs.components.createProduct.DisplayGenerationResultComponent
import com.benjtissot.sellingmugs.components.lists.ManageUsersComponent
import com.benjtissot.sellingmugs.entities.local.Category
import com.benjtissot.sellingmugs.entities.local.User
import com.benjtissot.sellingmugs.entities.openAI.CustomStatusCode
import com.benjtissot.sellingmugs.entities.openAI.GenerateCategoriesStatus
import com.benjtissot.sellingmugs.entities.openAI.GenerateCategoryStatus
import csstype.*
import emotion.react.css
import io.ktor.client.call.*
import io.ktor.http.*
import io.ktor.util.logging.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import mui.icons.material.PersonOutline
import mui.material.IconButton
import react.FC
import react.dom.html.ReactHTML.div
import react.useEffectOnce
import react.useState

private val LOG = KtorSimpleLogger("AdminPanelPage.kt")

val AdminPanelPage = FC<NavigationProps> { props ->
    var creationResult by useState(emptyList<CustomStatusCode>())
    /*var testCatStatus = GenerateCategoriesStatus("Id", listOf(
        GenerateCategoryStatus(
            Category("1", "Name 1"), "", listOf(
                CustomStatusCode(200, "Success"),
                CustomStatusCode(200, "Success"),
                CustomStatusCode(200, "Success"),
                CustomStatusCode(200, "Success"),
                CustomStatusCode(91, "This is an extremely long message. This is an extremely long message. This is an extremely long message. "),
            )),
        GenerateCategoryStatus(
            Category("2", "Name 2"), "There was an error during upload", listOf(
                CustomStatusCode(200, "Success"),
                CustomStatusCode(200, "Success"),
                CustomStatusCode(200, "Success"),
                CustomStatusCode(200, "Success"),
                CustomStatusCode(91, "This is an extremely long message. This is an extremely long message. This is an extremely long message. "),
            )),
        GenerateCategoryStatus(
            Category("2", "Name 2"), "", listOf(
                CustomStatusCode(200, "Success"),
                CustomStatusCode(200, "Success"),
                CustomStatusCode(200, "Success"),
                CustomStatusCode(200, "Success"),
                CustomStatusCode(91, "This is an extremely long message. This is an extremely long message. This is an extremely long message. "),
            )),
    ),
        Clock.System.now()
    )*/
    var generateCategoriesStatus : GenerateCategoriesStatus? by useState(null)

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

                if (creationResult.isNotEmpty()){
                    DisplayGenerationResultComponent {
                        list = creationResult
                        title = "Mug Generation Results"
                    }
                } else if (generateCategoriesStatus != null) {
                    DisplayCategoriesGenerationResultComponent {
                        list = generateCategoriesStatus!!.statuses
                        title  = "Advanced category generation results"
                    }
                } else {
                    CreateProductComponent {
                        navigate = props.navigate
                        session = props.session
                        onCreatingMugs = { subject, artType ->
                            props.setAlert(
                                infoAlert(
                                    "You are creating mugs on the subject of $subject in a ${artType.type} style",
                                    "Generating mugs",
                                    stayOn = true
                                )
                            )
                        }
                        onMugsCreationResponse = { httpResponse ->
                            when (httpResponse.status) {
                                HttpStatusCode.OK -> {
                                    scope.launch {
                                        val statusCodes: List<CustomStatusCode> = httpResponse.body()
                                        statusCodes.forEach { status ->
                                            println(status.print())
                                        }
                                        creationResult = statusCodes
                                    }
                                    props.setAlert(successAlert("You have successfully created your mugs"))
                                }

                                Const.HttpStatusCode_OpenAIUnavailable -> props.setAlert(errorAlert("OpenAI is unavailable, please try later", stayOn = true))
                                else -> props.setAlert(errorAlert("There has been a problem during creation. Consult Logs.", stayOn = true))
                            }

                        }
                        onCreatingCategories = { nbCat, nbVal, artType ->
                            props.setAlert(
                                infoAlert(
                                    "You are creating $nbCat categories with $nbVal mugs each. For style, you have chosen ${artType ?: "Automatic"}",
                                    "Generating categories and mugs",
                                    stayOn = true
                                )
                            )
                        }
                        onCategoriesCreationResponse = { httpResponse ->
                            when (httpResponse.status) {
                                HttpStatusCode.OK -> {
                                    scope.launch {
                                        val status: GenerateCategoriesStatus = httpResponse.body()
                                        status.statuses.forEach { generateCategoryStatus ->
                                            println("Category ${generateCategoryStatus.category.name}")
                                            generateCategoryStatus.customStatusCodes.forEach { statusCode ->
                                                println(statusCode.print())
                                            }
                                        }
                                        generateCategoriesStatus = status
                                    }
                                    props.setAlert(successAlert("You have successfully created your categories and your mugs"))
                                }

                                Const.HttpStatusCode_OpenAIUnavailable -> props.setAlert(errorAlert("OpenAI is unavailable, please try later", stayOn = true))
                                else -> props.setAlert(errorAlert("There has been a problem during creation. Consult Logs.", stayOn = true))
                            }

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