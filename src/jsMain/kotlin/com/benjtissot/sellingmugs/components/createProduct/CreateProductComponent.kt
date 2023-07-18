package com.benjtissot.sellingmugs.components.createProduct

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.highLevel.PopupHeaderComponent
import com.benjtissot.sellingmugs.components.forms.CreateProductForm
import com.benjtissot.sellingmugs.components.forms.GenerateMugsForm
import com.benjtissot.sellingmugs.entities.openAI.ChatRequestParams
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
import org.w3c.files.FileReader
import react.FC
import react.dom.html.ReactHTML
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.img
import react.router.useNavigate
import react.useState


private val LOG = KtorSimpleLogger("CreateProductComponent.kt")

external interface CreateProductProps : NavigationProps {
    var onCreatingMugs: (String, Const.StableDiffusionImageType) -> Unit
    var onMugsCreationResponse: (HttpStatusCode) -> Unit
}

val CreateProductComponent = FC<CreateProductProps> { props ->
    // Parent to hold flex to center the box
    div {
        css {
            display = Display.flex
            flexDirection = FlexDirection.column
            height = 100.pct
            width = 50.pct
            boxSizing = BoxSizing.borderBox
            paddingLeft = 2.vw
            paddingRight = 2.vw
        }

        // Box
        div {
            css {
                fontNormal()
                boxNormalNormal()
                boxShade()
                center()
                width = 95.pct
                padding = 4.vh
            }

            div {
                css {
                    fontBig()
                    contentCenteredVertically()
                    contentCenteredHorizontally()
                }
                +"Generate mugs by AI"
            }

            // Container for the centered content INSIDE the box
            div {
                css {
                    contentCenteredHorizontally()
                }


                GenerateMugsForm {
                    onSubmit = { subject, artType ->
                        props.onCreatingMugs(subject, artType)
                        scope.launch {
                            props.onMugsCreationResponse(createMugsForSubject(ChatRequestParams(subject, artType)))
                        }
                    }
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
