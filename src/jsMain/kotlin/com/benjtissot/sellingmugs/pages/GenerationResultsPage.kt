package com.benjtissot.sellingmugs.pages

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.createProduct.DisplayCategoriesGenerationResultComponent
import com.benjtissot.sellingmugs.components.createProduct.DisplayGenerationResultComponent
import com.benjtissot.sellingmugs.components.lists.GenerateResultItemComponent
import com.benjtissot.sellingmugs.components.lists.MugDetailsComplete
import com.benjtissot.sellingmugs.components.lists.MugDetailsHover
import com.benjtissot.sellingmugs.components.lists.MugItemGridComponent
import com.benjtissot.sellingmugs.entities.local.Mug
import com.benjtissot.sellingmugs.entities.openAI.GenerateCategoriesStatus
import csstype.*
import emotion.react.css
import io.ktor.client.call.*
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.util.logging.*
import kotlinx.coroutines.launch
import react.FC
import react.dom.html.ReactHTML.div
import react.router.useParams
import react.useEffectOnce
import react.useState

private val LOG = KtorSimpleLogger("GenerationResultsPage.kt")


val GenerationResultsPage = FC<NavigationProps> { props ->

    var generateResultsList : List<GenerateCategoriesStatus> by useState(emptyList())
    var selectedCategoriesStatus : GenerateCategoriesStatus? by useState(null)

    useEffectOnce {
        scope.launch {
            val httpResponse = getGenerateCategoriesStatusList()
            when (httpResponse.status){
                OK -> generateResultsList = httpResponse.body()
                else -> props.setAlert(errorAlert("Could not retrieve generation results"))
            }
        }
    }

    div {
        css {
            justifySpaceBetween()
            flexDirection = FlexDirection.row
            height = 100.pct
            width = 100.pct
        }

        // List for generation results
        div {
            css {
                display = Display.flex
                flexDirection = FlexDirection.column
                height = "fit-content".unsafeCast<Height>()
                maxHeight = 90.pct
                width = if (selectedCategoriesStatus != null) 45.pct else 100.pct
                boxSizing = BoxSizing.borderBox
                margin = 2.vw
            }

            // Box
            div {
                css {
                    fontNormal()
                    boxShade()
                    center()
                    height = 100.pct
                    width = 100.pct
                    paddingBottom = 2.vh
                    boxSizing = BoxSizing.borderBox
                    overflowY = "auto".unsafeCast<Overflow>()
                }
                div {
                    css {
                        componentTitle()
                        marginBottom = 2.pct
                    }
                    +"All results"
                }

                generateResultsList.forEach {
                    GenerateResultItemComponent {
                        index = generateResultsList.indexOf(it) + 1
                        generateCategoriesStatus = it
                        onClickShowDetails = {
                            selectedCategoriesStatus = it
                        }
                    }
                }

            }
        }

        if (selectedCategoriesStatus != null) {
            DisplayCategoriesGenerationResultComponent {
                status = selectedCategoriesStatus!!
                title  = "Advanced category generation results"
                width = 45.pct
            }
        }
    }



}
