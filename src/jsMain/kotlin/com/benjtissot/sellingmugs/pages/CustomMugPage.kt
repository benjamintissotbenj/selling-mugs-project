package com.benjtissot.sellingmugs.pages

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.createProduct.EditImageOnTemplateComponent
import com.benjtissot.sellingmugs.components.createProduct.ImageDrop
import com.benjtissot.sellingmugs.components.createProduct.SweepImageComponent
import com.benjtissot.sellingmugs.components.forms.CreateProductForm
import com.benjtissot.sellingmugs.components.forms.GenerateMugsForm
import com.benjtissot.sellingmugs.entities.openAI.GenerateCategoriesStatus
import com.benjtissot.sellingmugs.entities.openAI.MugsChatRequestParams
import com.benjtissot.sellingmugs.entities.printify.*
import csstype.*
import emotion.react.css
import io.ktor.client.call.*
import io.ktor.http.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mui.icons.material.AddShoppingCart
import mui.material.Button
import mui.material.Collapse
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

    var showAIForm by useState(true)

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
                    if (showAIForm){
                        contentCenteredVertically()
                        justifyContent = JustifyContent.center
                    } else {
                        contentCenteredHorizontally()
                    }
                }
                SweepImageComponent {
                    width = if (showAIForm) 15.vw else 20.vw
                    height = if (showAIForm) 15.vw else 20.vw
                    marginTop = null
                    srcList = if (uploadedImage != null) productPreviewImageSources else emptyList()
                    refresh = true
                    onClick = {
                        showAIForm = false
                    }
                    showPointer = !showAIForm
                    this.loading = loading
                }

                // Used as a spacer
                div {
                    css {
                        height = 1.vw
                        width = 1.vw
                    }
                }

                ImageDrop {
                    height = if (showAIForm) 10.vw else 10.vh
                    width = if (showAIForm) 15.vw else 20.vw
                    onImageDrop = { fileList ->
                        if (fileList.isNotEmpty()) {
                            scope.launch {
                                recordClick(props.session.clickDataId, Const.ClickType.CUSTOM_MUG_UPLOAD_IMAGE.type)
                            }
                            reader.abort()
                            val imageFile = fileList[0]
                            reader.readAsDataURL(imageFile)
                            reader.onload = { _ ->
                                loading = true
                                receiveProduct = null
                                uploadedImage = null
                                props.setAlert(infoAlert("Image is being uploaded"))
                                val uploadImage = ImageForUpload(
                                    file_name = imageFile.name,
                                    contents = selectBase64ContentFromURLData(reader.result as String)
                                )
                                scope.launch{
                                    val uploadReceive = uploadImage(uploadImage, public = props.session.user?.userType == Const.UserType.ADMIN)
                                    uploadReceive?.let {
                                        loading = false
                                        showAIForm = false
                                        uploadedImage = uploadReceive
                                        val mugProductInfo = MugProductInfo("Custom Mug - ${uploadReceive.id}", "", Const.mugCategoryDefault, it.toImage())
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
            else {
                div {
                    css {
                        width = 100.pct
                        textAlign = TextAlign.center
                        fontNormal()
                        fontWeight = FontWeight.bold
                        padding = 2.vh
                    }
                    +"Or : create your design using AI"
                }
                //Create via AI
                Collapse {
                    css {
                        height = "fit-content".unsafeCast<Height>()
                        width = 90.pct
                        boxSizing = BoxSizing.borderBox
                        padding
                    }
                    `in` = showAIForm
                    GenerateMugsForm {
                        width = 100.pct
                        isCustomMug = true
                        onSubmit = { subject, artType, _ ->
                            props.setAlert(
                                infoAlert(
                                    "You are creating an image on the subject of $subject in a ${artType.type} style",
                                    "Generating image",
                                    stayOn = true
                                )
                            )
                            receiveProduct = null
                            uploadedImage = null
                            loading = true
                            scope.launch {
                                recordClick(props.session.clickDataId, Const.ClickType.GENERATE_CUSTOM_DESIGN_BUTTON.type)
                                // API call to generate design, receive an Image For Upload Receive
                                val httpResponse = generateDesign(MugsChatRequestParams(subject, artType, 1))
                                when (httpResponse.status) {
                                    HttpStatusCode.OK -> {
                                        scope.launch {
                                            val uploadReceive : ImageForUploadReceive = httpResponse.body()
                                            loading = false
                                            showAIForm = false
                                            delay(25L)
                                            uploadedImage = uploadReceive
                                            val mugProductInfo = MugProductInfo("Custom Mug - ${uploadReceive.id}", "", Const.mugCategoryDefault, uploadReceive.toImage())
                                            val httpResponseProduct = createProduct(mugProductInfo)
                                            val productId = httpResponseProduct.body<String>()

                                            if (httpResponseProduct.status != HttpStatusCode.OK){
                                                props.setAlert(errorAlert("Mug with custom AI generated design could not be created."))
                                                return@launch
                                            } else {
                                                publishProduct(productId)
                                                props.setAlert(successAlert("Mug with custom AI generated design was created successfully !"))
                                                receiveProduct = getProduct(productId)
                                                loading = false
                                            }
                                        }
                                        props.setAlert(successAlert("You have successfully created your design"))
                                    }

                                    Const.HttpStatusCode_OpenAIUnavailable -> props.setAlert(errorAlert("OpenAI is unavailable, please try later", stayOn = true))
                                    else -> props.setAlert(errorAlert("There has been a problem during the generation process. Please try again. If the problem persists, contact an administrator.", stayOn = true))
                                }

                            }
                        }
                    }
                }
                Collapse {
                    `in` = !showAIForm
                    Button {
                        +"Use AI"
                        onClick = {
                            showAIForm = true
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
