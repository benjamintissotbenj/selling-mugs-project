package com.benjtissot.sellingmugs.pages

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.EditImageOnTemplateComponent
import com.benjtissot.sellingmugs.components.HoverImageComponent
import com.benjtissot.sellingmugs.components.SweepImageComponent
import com.benjtissot.sellingmugs.components.createProduct.ImageDrop
import com.benjtissot.sellingmugs.entities.printify.Image
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
import mui.icons.material.Refresh
import mui.material.IconButton
import org.w3c.files.FileReader
import react.FC
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.img
import react.useEffectOnce
import react.useState
import kotlin.random.Random

private val LOG = KtorSimpleLogger("CustomMugPage.kt")


val CustomMugPage = FC<NavigationProps> { props ->
    var receiveProduct : ReceiveProduct? by useState(null)
    var droppedImageName: String = ""
    var uploadedImage : ImageForUploadReceive? by useState(null)
    val productPreviewImageSources : List<String> = receiveProduct?.images?.map{it.src} ?: emptyList()
    val reader = FileReader()

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
            SweepImageComponent {
                width = 20.vw
                height = 20.vw
                srcList = productPreviewImageSources
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
                        reader.abort()
                        val imageFile = fileList[0]
                        droppedImageName = imageFile.name
                        reader.readAsDataURL(imageFile)
                        reader.onload = { _ ->
                            val uploadImage = ImageForUpload(
                                file_name = droppedImageName,
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
                    this.receiveProduct = receiveProduct
                    this.updateProduct = {
                        receiveProduct = it
                    }
                }
            }
        }
    }

}
