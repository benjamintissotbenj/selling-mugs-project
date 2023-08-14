package com.benjtissot.sellingmugs.pages

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.buttons.LogoutButtonComponent
import com.benjtissot.sellingmugs.components.createProduct.CreateProductComponent
import com.benjtissot.sellingmugs.components.createProduct.DisplayCategoriesGenerationResultComponent
import com.benjtissot.sellingmugs.components.createProduct.DisplayGenerationResultComponent
import com.benjtissot.sellingmugs.components.lists.ManageUsersComponent
import com.benjtissot.sellingmugs.entities.local.User
import com.benjtissot.sellingmugs.entities.openAI.*
import csstype.*
import emotion.react.css
import io.ktor.client.call.*
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.util.logging.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.js.timers.Timeout
import kotlinx.js.timers.clearInterval
import kotlinx.js.timers.setInterval
import mui.icons.material.DataThresholding
import mui.icons.material.PersonOutline
import mui.material.IconButton
import react.FC
import react.dom.html.ReactHTML.div
import react.useEffect
import react.useEffectOnce
import react.useState

private val LOG = KtorSimpleLogger("AdminPanelPage.kt")

val AdminPanelPage = FC<NavigationProps> { props ->
    var creationResult by useState(emptyList<CustomStatusCode>())
    /*
    var testCatStatus = GenerateCategoriesStatus("Id", "MessageAtTop", CategoriesChatRequestParams(3, 3, null), listOf(
        GenerateCategoryStatus(
            Category("1", "Name 1"), "", listOf(
                CustomStatusCode(200, "Success"),
                CustomStatusCode(200, "Success"),
                CustomStatusCode(200, "Success"),
                CustomStatusCode(200, "Success"),
                CustomStatusCode(91, "This is an extremely long message. This is an extremely long message. This is an extremely long message. "),
            ), Clock.System.now(), Clock.System.now()),
        GenerateCategoryStatus(
            Category("2", "Name 2"), "There was an error during upload", listOf(
                CustomStatusCode(200, "Success"),
                CustomStatusCode(200, "Success"),
                CustomStatusCode(200, "Success"),
                CustomStatusCode(200, "Success"),
                CustomStatusCode(91, "This is an extremely long message. This is an extremely long message. This is an extremely long message. "),
            ), Clock.System.now(), Clock.System.now()),
        GenerateCategoryStatus(
            Category("2", "Name 2"), "", listOf(
                CustomStatusCode(200, "Success"),
                CustomStatusCode(200, "Success"),
                CustomStatusCode(200, "Success"),
                CustomStatusCode(200, "Success"),
                CustomStatusCode(91, "This is an extremely long message. This is an extremely long message. This is an extremely long message. "),
            ), Clock.System.now(), Clock.System.now()),
    ),
        Clock.System.now(), Clock.System.now()
    )
    */
    var generateCategoriesStatus : GenerateCategoriesStatus? by useState(null)
    var getGenerateCategoriesStatusTimeout: Timeout? by useState(null)

    // TODO : every two seconds, query to get an updated object
    // TODO in backend, return an object when created, update the object at every stage of the process (much better)
    useEffectOnce {
        scope.launch {
        }
    }

    useEffect {
        if (generateCategoriesStatus != null && generateCategoriesStatus!!.pending) {
            if (getGenerateCategoriesStatusTimeout == null) {
                getGenerateCategoriesStatusTimeout = setInterval({
                    scope.launch {
                        val httpResponse = getGenerateCategoriesStatus(generateCategoriesStatus!!.id)
                        when (httpResponse.status){
                            // TODO: issue with update here, current percentages not working for some reason
                            OK -> {
                                val temp = httpResponse.body<GenerateCategoriesStatus>()
                                val currentCatStat = generateCategoriesStatus!! // for some reason, percentages optimised out of JS if we don't do this
                                val newSuccessPercentage =  temp.calculateSuccessPercentage()
                                val currentSuccessPercentage = currentCatStat.calculateSuccessPercentage()
                                val currentCompletionPercentage = currentCatStat.calculateCompletionPercentage()
                                when (val newCompletionPercentage = temp.calculateCompletionPercentage()){
                                    100 -> {
                                        props.setAlert(successAlert("You have successfully your mugs with $newCompletionPercentage%!", stayOn = true))
                                        generateCategoriesStatus = temp
                                    }
                                    0 -> generateCategoriesStatus = temp
                                    else -> {
                                        if (newCompletionPercentage > currentCompletionPercentage ||
                                            (newCompletionPercentage == currentCompletionPercentage && newSuccessPercentage > currentSuccessPercentage)
                                        ){
                                            props.setAlert(infoAlert("Updated info on the categories : $newCompletionPercentage% completion, $newSuccessPercentage% success", stayOn = true))
                                            LOG.debug("Old compl = $currentCompletionPercentage, new compl = $newCompletionPercentage")
                                            generateCategoriesStatus = temp
                                            delay(100L)
                                        }
                                    }
                                }
                            }
                            BadRequest -> props.setAlert(errorAlert("Bad request"))
                            else -> props.setAlert(errorAlert("Something went wrong, check the logs"))
                        }
                    }
                }, 2000)
            }
        } else {
            getGenerateCategoriesStatusTimeout?.let { clearInterval(it) }
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
                        status = generateCategoriesStatus!!
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
                        onCreatingCategories = { nbCat, nbVal, artType, newCatOnly ->
                            props.setAlert(
                                infoAlert(
                                    "You are creating $nbCat ${if (newCatOnly) "new " else ""}categories with $nbVal mugs each. For style, you have chosen ${artType ?: "Automatic"}",
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
                                        generateCategoriesStatus = status
                                    }
                                    props.setAlert(infoAlert("You are now creating your categories and your mugs. Waiting for an update.", stayOn = true))
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
                        css {
                            marginRight = 1.vh
                        }
                        +"Open User Info"
                    }
                    PersonOutline()
                    onClick = {
                        props.navigate.invoke(USER_INFO_PATH)
                    }
                }

                IconButton {
                    div {
                        css {
                            marginRight = 1.vh
                        }
                        +"Show generation results"
                    }
                    DataThresholding()
                    onClick = {
                        props.navigate.invoke(GENERATION_RESULTS_PATH)
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