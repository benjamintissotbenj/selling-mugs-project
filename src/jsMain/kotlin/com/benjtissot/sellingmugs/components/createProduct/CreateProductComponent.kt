package com.benjtissot.sellingmugs.components.createProduct

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.highLevel.PopupHeaderComponent
import com.benjtissot.sellingmugs.components.forms.CreateProductForm
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
import org.w3c.files.FileReader
import react.FC
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.img
import react.useState


private val LOG = KtorSimpleLogger("CreateProductComponent.kt")

external interface CreateProductProps : NavigationProps {
    var onProductCreatedSuccess : (productId: String, productName: String) -> Unit
    var onProductCreatedFailed : (productId: String) -> Unit
    var onClickClose: () -> Unit
}

val CreateProductComponent = FC<CreateProductProps> { props ->
    var uploadedImage: ImageForUploadReceive? by useState(null) // Starts blank but not empty, as to not show warning message initially

    val reader = FileReader()
    // Parent to hold flex to center the box
    div {
        css {
            display = Display.flex
            flexDirection = FlexDirection.column
            width = 100.pct
        }



        // Box
        div {
            css {
                fontNormal()
                boxNormalNormal()
                boxShade()
                center()
                padding = 1.vh
            }

            PopupHeaderComponent {
                onClickClose = { props.onClickClose() }
                title = "Create a product"
            }

            // Container for the centered content INSIDE the box
            div {
                css {
                    contentCenteredHorizontally()
                }

                ImageDrop {
                    onImageDrop = { fileList ->
                        if (fileList.isNotEmpty()) {
                            LOG.debug("Image Was Dropped")
                            LOG.debug("File List: $fileList")

                            val imageFile = fileList[0]
                            reader.readAsDataURL(imageFile)
                            reader.onload = { _ ->
                                props.setAlert(infoAlert("Image is being uploaded"))
                                val uploadImage = ImageForUpload(
                                    file_name = imageFile.name,
                                    contents = selectBase64ContentFromURLData(reader.result as String)
                                )
                                scope.launch {
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

                uploadedImage?.let {
                    img {
                        src = uploadedImage?.preview_url ?: ""
                        // Styles for the product image
                        css {
                            width = 80.px
                            height = 80.px
                            marginRight = 16.px
                        }
                    }
                }


                CreateProductForm {
                    onSubmit = { title, description ->
                        scope.launch {// Data processing to create the product in Printify store
                            uploadedImage?.let {
                                val mugProductInfo = MugProductInfo(title, description, uploadedImage!!.toImage())
                                val httpResponse = createProduct(mugProductInfo)
                                val productId = httpResponse.body<String>()

                                if (httpResponse.status != HttpStatusCode.OK){
                                    props.onProductCreatedFailed(productId)
                                    return@launch
                                } else {
                                    props.onProductCreatedSuccess(productId, title)
                                    publishProduct(productId)
                                }

                            }?:let{
                                props.setAlert(errorAlert("Please upload an image before creating a product"))
                                return@launch
                            }

                        }
                    }
                    deleteFieldsOnSubmit = (uploadedImage!=null)
                }
            }


        }
    }
}
