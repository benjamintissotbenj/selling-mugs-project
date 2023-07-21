package com.benjtissot.sellingmugs.components.createProduct

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.forms.GenerateCategoriesForm
import com.benjtissot.sellingmugs.components.forms.GenerateMugsForm
import com.benjtissot.sellingmugs.components.highLevel.CreateTabsComponent
import com.benjtissot.sellingmugs.entities.openAI.CategoriesChatRequestParams
import com.benjtissot.sellingmugs.entities.openAI.MugsChatRequestParams
import csstype.*
import emotion.react.css
import io.ktor.client.statement.*
import io.ktor.util.logging.*
import kotlinx.coroutines.launch
import mui.lab.TabPanel
import mui.material.Button
import react.FC
import react.dom.html.ReactHTML.div


private val LOG = KtorSimpleLogger("CreateProductComponent.kt")

external interface CreateProductProps : NavigationProps {
    var onCreatingMugs: (String, Const.StableDiffusionImageType) -> Unit
    var onMugsCreationResponse: (HttpResponse) -> Unit
    var onCreatingCategories: (Int, Int, Const.StableDiffusionImageType?) -> Unit
    var onCategoriesCreationResponse: (HttpResponse) -> Unit
}

val CreateProductComponent = FC<CreateProductProps> { props ->
    // Parent to hold flex to center the box
    div {
        css {
            display = Display.flex
            flexDirection = FlexDirection.column
            height = "fit-content".unsafeCast<Height>()
            maxHeight = 90.pct
            width = 50.pct
            boxSizing = BoxSizing.borderBox
            paddingLeft = 2.vw
            paddingRight = 2.vw
            margin = 2.vw
        }

        // Box
        div {
            css {
                fontNormal()
                boxNormalNormal()
                boxShade()
                center()
                height = 100.pct
                width = 100.pct
                paddingInline = 2.vw
                paddingBottom = 2.vh
                overflowY = "auto".unsafeCast<Overflow>()
            }

            CreateTabsComponent {
                height = "fit-content".unsafeCast<Height>()
                maxHeight = 80.pct
                width = 100.pct
                labels = listOf("AI Generated mugs", "Advanced")
                onClickTab = { value ->
                    when (value) {
                        0 -> scope.launch {
                            recordClick(props.session.clickDataId, Const.ClickType.GENERATE_MUGS_TAB.type)
                        }
                        1 -> scope.launch {
                            recordClick(props.session.clickDataId, Const.ClickType.GENERATE_CATEGORIES_TAB.type)
                        }
                    }
                }

                TabPanel {
                    css {
                        boxSizing = BoxSizing.borderBox
                        width = 100.pct
                        height = 100.pct
                    }
                    value = "0"

                    // Container for the centered content INSIDE the box
                    div {
                        css {
                            contentCenteredHorizontally()
                        }


                        GenerateMugsForm {
                            onSubmit = { subject, artType, numberOfVariations ->
                                props.onCreatingMugs(subject, artType)
                                scope.launch {
                                    recordClick(props.session.clickDataId, Const.ClickType.GENERATE_MUGS_BUTTON.type)
                                    props.onMugsCreationResponse(generateMugs(MugsChatRequestParams(subject, artType, numberOfVariations)))
                                }
                            }
                        }
                    }
                }

                TabPanel {
                    css {
                        tabPanel()
                    }
                    value = "1"


                    // Container for the centered content INSIDE the box
                    div {
                        css {
                            contentCenteredHorizontally()
                        }

                        GenerateCategoriesForm {
                            onSubmit = { nbCat, nbVar, artType ->
                                println("TODO: $nbCat, $nbVar, ${artType?.type ?: "Surprise me"}")
                                props.onCreatingCategories(nbCat, nbVar, artType)
                                scope.launch {
                                    recordClick(props.session.clickDataId, Const.ClickType.GENERATE_CATEGORIES_BUTTON.type)
                                    props.onCategoriesCreationResponse(generateMugsInCategories(CategoriesChatRequestParams(nbCat, nbVar, artType)))
                                }
                            }
                        }
                    }
                }
            }


            div {
                css {
                    height = 20.pct
                    display = Display.flex
                    flexDirection = FlexDirection.column
                    alignItems = AlignItems.center
                }
                div {
                    css {
                        fontNormal()
                        fontWeight = FontWeight.bold
                        margin = 2.vh
                    }
                    +"OR"
                }

                Button {
                    +"Custom mug creation"
                    onClick = {
                        props.navigate.invoke(CUSTOM_MUG_PATH)
                    }
                }
            }
        }
    }
}
