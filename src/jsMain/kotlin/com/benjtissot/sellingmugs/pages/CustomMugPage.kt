package com.benjtissot.sellingmugs.pages

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.createProduct.EditImageOnTemplateComponent
import com.benjtissot.sellingmugs.components.createProduct.SweepImageComponent
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
import mui.icons.material.AddShoppingCart
import mui.material.IconButton
import org.w3c.files.FileReader
import react.FC
import react.dom.html.ReactHTML.div
import react.useEffectOnce
import react.useState

private val LOG = KtorSimpleLogger("CustomMugPage.kt")


val CustomMugPage = FC<NavigationProps> { props ->
    var receiveProduct : ReceiveProduct? by useState(null)
    var droppedImageName: String = ""
    var uploadedImage : ImageForUploadReceive? by useState(null)
    val productPreviewImageSources : List<String> = receiveProduct?.images?.map{it.src} ?: emptyList()
    val reader = FileReader()
    var loading by useState(false)

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

        // Image and drag-and-drop container
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

            ImageDrop {
                height = 20.vh
                width = 30.vw
                onImageDrop = { fileList ->
                    if (fileList.isNotEmpty()) {
                        reader.abort()
                        val imageFile = fileList[0]
                        droppedImageName = imageFile.name
                        reader.readAsDataURL(imageFile)
                        reader.onload = { _ ->
                            props.setAlert(infoAlert("Image is being uploaded"))
                            val uploadImage = ImageForUpload(
                                file_name = droppedImageName,
                                contents = selectBase64ContentFromURLData(reader.result as String)
                            )
                            scope.launch{
                                val uploadReceive = uploadImage(uploadImage, public = props.session.user?.userType == Const.UserType.ADMIN)
                                uploadReceive?.let {

                                    uploadedImage = uploadReceive
                                    // TODO allow admin to set name and description
                                    val mugProductInfo = MugProductInfo("Custom ImageID-${uploadReceive.id}", "", it.toImage())
                                    val httpResponse = createProduct(mugProductInfo)
                                    val productId = httpResponse.body<String>()

                                    if (httpResponse.status != HttpStatusCode.OK){
                                        props.setAlert(errorAlert("Mug with image ${uploadImage.file_name} could not be created."))
                                        return@launch
                                    } else {
                                        publishProduct(productId)
                                        props.setAlert(successAlert("Mug with image ${uploadImage.file_name} was created successfully !"))
                                        // LOG.debug("Mug with image ${uploadImage.file_name} was created successfully with id $productId")
                                        receiveProduct = getProduct(productId)
                                        loading = false
                                    }
                                } ?: let {
                                    props.setAlert(errorAlert("Image ${uploadImage.file_name} could not be uploaded."))
                                }
                            }
                        }
                    }
                }
            }


            receiveProduct?.let {
                IconButton {
                    AddShoppingCart()
                    div {
                        css {
                            fontNormal()
                            margin = 2.vw
                        }
                        +"Add to cart"
                    }
                    onClick = {
                        // Add product to cart
                        scope.launch {
                            val mug = getMugByPrintifyId(receiveProduct!!.id)
                            mug?.let {
                                addMugToCart(it)
                                props.setAlert(successAlert("Mug added to card !"))
                            } ?: let {
                                props.setAlert(errorAlert())
                            }
                        }
                    }
                }
            }
        }

        // Creation of the custom mug
        div {
            css {
                width = 67.pct
                marginTop = 4.vh
                contentCenteredHorizontally()
            }
            div {
                css {
                    boxNormalBig()
                    maxHeight = "fit-content".unsafeCast<MaxHeight>()
                    margin = "auto".unsafeCast<Margin>()
                    boxShade()
                    contentCenteredVertically()
                    contentCenteredHorizontally()
                }

                EditImageOnTemplateComponent {
                    this.uploadedImage = uploadedImage
                    this.receiveProduct = receiveProduct
                    this.updateProduct = {
                        props.setAlert(infoAlert("Updating preview images"))
                        receiveProduct = it
                    }
                }
            }
        }
    }

}
