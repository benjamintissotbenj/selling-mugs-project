package com.benjtissot.sellingmugs.pages

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.createProduct.EditImageOnTemplateComponent
import com.benjtissot.sellingmugs.components.createProduct.SweepImageComponent
import com.benjtissot.sellingmugs.components.createProduct.ImageDrop
import com.benjtissot.sellingmugs.components.forms.CreateProductForm
import com.benjtissot.sellingmugs.entities.printify.*
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
                height = 100.pct
                marginLeft = 2.vw
                contentCenteredHorizontally()
            }
            
            SweepImageComponent {
                width = 20.vw
                height = 20.vw
                marginTop = if (props.session.user?.userType != Const.UserType.ADMIN) { 4.vh } else null
                srcList = productPreviewImageSources
                refresh = true
            }

            ImageDrop {
                height = 20.vh
                width = 30.vw
                onImageDrop = { fileList ->
                    if (fileList.isNotEmpty()) {
                        scope.launch {
                            recordClick(props.session.clickDataId, Const.ClickType.CUSTOM_MUG_UPLOAD_IMAGE.type)
                        }
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
                                    val mugProductInfo = MugProductInfo("Custom Mug - ${uploadReceive.id}", "", it.toImage())
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

            if (props.session.user?.userType == Const.UserType.ADMIN) {
                CreateProductForm {
                    onSubmit = { title, description ->
                        scope.launch {// Data processing to create the product in Printify store
                            uploadedImage?.let {
                                scope.launch {
                                    receiveProduct = receiveProduct?.let {
                                        putProduct(it.id, UpdateProductTitleDesc(title = title, description = description))
                                    }
                                }
                                props.setAlert(infoAlert("Updating title and description"))

                            }?:let{
                                props.setAlert(errorAlert("Please upload an image before creating a product"))
                                return@launch
                            }

                        }
                    }
                    deleteFieldsOnSubmit = false
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
                            recordClick(props.session.clickDataId, Const.ClickType.CUSTOM_MUG_ADD_TO_CART.type)
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
                        scope.launch {
                            recordClick(props.session.clickDataId, Const.ClickType.CUSTOM_MUG_REFRESH_PREVIEW.type)
                        }
                        props.setAlert(infoAlert("Updating preview images"))
                        receiveProduct = it
                    }
                }
            }
        }
    }

}
