package com.benjtissot.sellingmugs.pages

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.HoverImageComponent
import com.benjtissot.sellingmugs.components.createProduct.ImageDrop
import com.benjtissot.sellingmugs.components.highLevel.FooterComponent
import com.benjtissot.sellingmugs.components.highLevel.NavigationBarComponent
import com.benjtissot.sellingmugs.components.lists.CartListComponent
import com.benjtissot.sellingmugs.entities.printify.ImageForUpload
import com.benjtissot.sellingmugs.entities.printify.ImageForUploadReceive
import csstype.*
import emotion.react.css
import io.ktor.util.logging.*
import kotlinx.coroutines.launch
import mui.icons.material.Payment
import mui.material.IconButton
import org.w3c.files.FileReader
import react.FC
import react.dom.html.ReactHTML.div
import react.router.useNavigate
import react.useEffectOnce
import react.useState

private val LOG = KtorSimpleLogger("CustomMugPage.kt")

val CustomMugPage = FC<NavigationProps> { props ->
    var uploadedImage: ImageForUploadReceive? by useState(null) // Starts blank but not empty, as to not show warning message initially

    useEffectOnce {
        scope.launch {

        }
    }

    div {
        css {
            contentCenteredVertically()
            height = 100.pct
            width = 100.pct
        }

        // Image container
        div {
            css {
                width = 33.pct
                contentCenteredHorizontally()
            }
            HoverImageComponent {
                width = 10.rem
                height = 10.rem
                srcMain = "https://images.printify.com/api/catalog/5e440fbfd897db313b1987d1.jpg?s=320"
                srcHover = "https://images.printify.com/api/catalog/6358ee8d99b22ccab005e8a7.jpg?s=320"
                onClick = {
                    props.navigate.invoke(CUSTOM_MUG_PATH)
                }
            }
        }

        div {
            css {
                width = 67.pct
                contentCenteredVertically()
                contentCenteredHorizontally()
            }
            div {
                css {
                    boxNormalNormal()
                    boxShade()
                    contentCenteredVertically()
                    contentCenteredHorizontally()
                }
                ImageDrop {
                    onImageDrop = { fileList ->
                        LOG.debug("Image Was Dropped")
                        LOG.debug("File List: $fileList")
                        val imageFile = fileList[0]
                        val reader = FileReader()
                        reader.readAsDataURL(imageFile)
                        reader.onload = { _ ->
                            val uploadImage = ImageForUpload(
                                file_name = imageFile.name,
                                contents = selectBase64ContentFromURLData(reader.result as String)
                            )
                            scope.launch{
                                val uploadReceive = uploadImage(uploadImage)
                                uploadReceive?.let {
                                    props.setAlert(successAlert("Image ${uploadImage.file_name} was uploaded successfully !"))
                                } ?: let {
                                    props.setAlert(errorAlert("Image ${uploadImage.file_name} could not be uploaded."))
                                }
                                uploadedImage = uploadReceive
                            }
                        }
                    }
                }
            }
        }
    }

}
