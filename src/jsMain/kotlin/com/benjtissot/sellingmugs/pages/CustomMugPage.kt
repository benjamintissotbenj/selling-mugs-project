package com.benjtissot.sellingmugs.pages

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.createProduct.EditImageOnTemplateComponent
import com.benjtissot.sellingmugs.components.createProduct.ImageDrop
import com.benjtissot.sellingmugs.components.createProduct.SweepImageComponent
import com.benjtissot.sellingmugs.components.forms.CreateProductForm
import com.benjtissot.sellingmugs.components.forms.GenerateMugsForm
import com.benjtissot.sellingmugs.entities.openAI.ChatRequestParams
import com.benjtissot.sellingmugs.entities.printify.*
import csstype.*
import emotion.react.css
import io.ktor.client.call.*
import io.ktor.http.*
import kotlinx.coroutines.launch
import mui.icons.material.AddShoppingCart
import mui.material.IconButton
import org.w3c.files.FileReader
import react.FC
import react.dom.html.ReactHTML.div
import react.useEffectOnce
import react.useState

/*private val LOG = KtorSimpleLogger("CustomMugPage.kt")*/


val CustomMugPage = FC<NavigationProps> { props ->
    var receiveProduct : ReceiveProduct? by useState(null)
    var uploadedImage : ImageForUploadReceive? by useState(null)
    val productPreviewImageSources : List<String> = receiveProduct?.images?.map{it.src} ?: emptyList()
    val reader = FileReader()
    var loading by useState(false)

    val isAdmin = props.session.user?.userType == Const.UserType.ADMIN

    useEffectOnce {
        scope.launch {

        }
    }

    div {
        css {
            contentCenteredVertically()
            height = 100.pct
            width = 98.pct
        }

        // Image and drag-and-drop container
        div {
            css {
                width = 40.pct
                height = 100.pct
                contentCenteredHorizontally()
            }

            div {
                css {
                    width = 100.pct
                    if (isAdmin){
                        contentCenteredVertically()
                        justifyContent = JustifyContent.center
                    } else {
                        contentCenteredHorizontally()
                    }
                }
                SweepImageComponent {
                    width = if (isAdmin) 15.vw else 20.vw
                    height = if (isAdmin) 15.vw else 20.vw
                    marginTop = if (props.session.user?.userType != Const.UserType.ADMIN) { 4.vh } else null
                    srcList = productPreviewImageSources
                    refresh = true
                }

                // Used as a spacer
                div {
                    css {
                        height = 1.vw
                        width = 1.vw
                    }
                }

                ImageDrop {
                    height = if (isAdmin) 10.vw else 20.vh
                    width = if (isAdmin) 15.vw else 30.vw
                    onImageDrop = { fileList ->
                        if (fileList.isNotEmpty()) {
                            scope.launch {
                                recordClick(props.session.clickDataId, Const.ClickType.CUSTOM_MUG_UPLOAD_IMAGE.type)
                            }
                            reader.abort()
                            val imageFile = fileList[0]
                            reader.readAsDataURL(imageFile)
                            reader.onload = { _ ->
                                props.setAlert(infoAlert("Image is being uploaded"))
                                val uploadImage = ImageForUpload(
                                    file_name = imageFile.name,
                                    contents = selectBase64ContentFromURLData(reader.result as String)
                                )
                                scope.launch{
                                    val uploadReceive = uploadImage(uploadImage, public = props.session.user?.userType == Const.UserType.ADMIN)
                                    uploadReceive?.let {

                                        uploadedImage = uploadReceive
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
            }



            if (isAdmin) {
                CreateProductForm {
                    onSubmit = { title, description ->
                        scope.launch {// Data processing to create the product in Printify store
                            uploadedImage?.let {
                                props.setAlert(infoAlert("Updating title and description"))
                                scope.launch {
                                    val receiveProductTemp = receiveProduct?.let {
                                        putProduct(it.id, UpdateProductTitleDesc(title = title, description = description))
                                    }
                                    receiveProductTemp?.let {
                                        props.setAlert(successAlert("Title and description were successfully updated"))
                                        getMugByPrintifyId(receiveProduct!!.id)?.let { mug ->
                                            addMugToUserCustomMugList(props.session.user?.id ?: "", mug.id)
                                        }
                                    } ?: let {
                                        props.setAlert(errorAlert("Could not update title and description of the product"))
                                    }
                                    receiveProduct = receiveProductTemp
                                }

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
                                addMugToUserCustomMugList(props.session.user?.id ?: "", it.id)
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
                width = 60.pct
                marginTop = 2.vh
                contentCenteredHorizontally()
            }
            div {
                css {
                    boxNormalBig()
                    maxHeight = "fit-content".unsafeCast<MaxHeight>()
                    boxSizing = BoxSizing.borderBox
                    boxShade()
                    contentCenteredVertically()
                    contentCenteredHorizontally()
                }

                EditImageOnTemplateComponent {
                    this.uploadedImage = uploadedImage
                    this.receiveProduct = receiveProduct
                    this.setAlert = props.setAlert
                    this.updateProduct = { receiveProductTemp ->
                        scope.launch {
                            recordClick(props.session.clickDataId, Const.ClickType.CUSTOM_MUG_REFRESH_PREVIEW.type)
                        }
                        receiveProductTemp?.let {
                            props.setAlert(successAlert("Successfully updated preview images"))
                        } ?: let {
                            props.setAlert(errorAlert("Could not update preview images "))
                        }
                        receiveProduct = receiveProductTemp
                    }
                }
            }
        }
    }

}
