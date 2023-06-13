package com.benjtissot.sellingmugs.pages

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.EditImageOnTemplateComponent
import com.benjtissot.sellingmugs.components.HoverImageComponent
import com.benjtissot.sellingmugs.components.createProduct.ImageDrop
import com.benjtissot.sellingmugs.entities.printify.ImageForUpload
import com.benjtissot.sellingmugs.entities.printify.ImageForUploadReceive
import com.benjtissot.sellingmugs.entities.printify.MugProductInfo
import com.benjtissot.sellingmugs.entities.printify.ReceiveProduct
import csstype.*
import emotion.react.css
import io.ktor.client.call.*
import io.ktor.http.*
import io.ktor.util.logging.*
import kotlinx.coroutines.launch
import org.w3c.files.FileReader
import react.FC
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.img
import react.useEffectOnce
import react.useState

private val LOG = KtorSimpleLogger("CustomMugPage.kt")

val CustomMugPage = FC<NavigationProps> { props ->
    var receiveProduct : ReceiveProduct? by useState(null)
    var uploadedImage : ImageForUploadReceive? by useState(null)
    val productPreviewImageSources : List<String> = receiveProduct?.images?.map{it.src} ?: emptyList()

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
                width = 20.vw
                height = 20.vw
                srcMain = "https://images.printify.com/api/catalog/5e440fbfd897db313b1987d1.jpg?s=320"
                srcHover = "https://images.printify.com/api/catalog/6358ee8d99b22ccab005e8a7.jpg?s=320"
                onClick = {
                    props.navigate.invoke(CUSTOM_MUG_PATH)
                }
            }
        }

        // Creation of the custom mug
        div {
            css {
                width = 67.pct
                contentCenteredVertically()
                contentCenteredHorizontally()
            }
            div {
                css {
                    boxNormalBig()
                    boxShade()
                    contentCenteredVertically()
                    contentCenteredHorizontally()
                }
                ImageDrop {
                    onImageDrop = { fileList ->
                        val imageFile = fileList[0]
                        val reader = FileReader()
                        reader.readAsDataURL(imageFile)
                        reader.onload = { _ ->
                            val uploadImage = ImageForUpload(
                                file_name = imageFile.name,
                                contents = selectBase64ContentFromURLData(reader.result as String)
                            )
                            scope.launch{
                                val uploadReceive = uploadImage(uploadImage, public = false)
                                uploadReceive?.let {

                                    uploadedImage = uploadReceive

                                    val mugProductInfo = MugProductInfo("Custom ${uploadReceive.id}", "", it.toImage())
                                    val httpResponse = createProduct(mugProductInfo)
                                    val productId = httpResponse.body<String>()

                                    if (httpResponse.status != HttpStatusCode.OK){
                                        props.setAlert(errorAlert("Mug with image ${uploadImage.file_name} could not be created."))
                                        return@launch
                                    } else {
                                        publishProduct(productId)
                                        props.setAlert(successAlert("Mug with image ${uploadImage.file_name} was created successfully !"))
                                        receiveProduct = getProduct(productId)
                                    }
                                } ?: let {
                                    props.setAlert(errorAlert("Image ${uploadImage.file_name} could not be uploaded."))
                                }
                            }
                        }
                    }
                }

                EditImageOnTemplateComponent {
                    this.uploadedImage = uploadedImage
                }

            }
        }
    }

    // Image Preview List
    div {
        css {
            contentCenteredVertically()
        }
        productPreviewImageSources.forEach {
            img {
                css {
                    width = 10.vw
                    height = 10.vw
                }
                src = it
            }
        }
    }

}
