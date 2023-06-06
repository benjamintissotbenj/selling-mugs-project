package com.benjtissot.sellingmugs.components

import ImageDrop
import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.entities.printify.*
import com.benjtissot.sellingmugs.entities.printify.Image
import com.benjtissot.sellingmugs.pages.selectBase64ContentFromURLData
import csstype.*
import emotion.react.css
import io.ktor.client.call.*
import io.ktor.http.*
import io.ktor.util.logging.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import mui.icons.material.PostAdd
import mui.material.IconButton
import org.w3c.files.File
import org.w3c.files.FileReader
import react.FC
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.img
import react.useState


private val LOG = KtorSimpleLogger("CreateProductComponent.kt")

external interface CreateProductProps : NavigationProps {
    var onProductCreatedSuccess : (productId: String) -> Unit
    var onProductCreatedFailed : (productId: String) -> Unit
}

val CreateProductComponent = FC<CreateProductProps> { props ->
    var imageDropped : Image? by useState(null)
    var uploadedImageUrl: String by useState("")

    // Parent to hold flex to center the box
    div {
        css {
            display = Display.flex
            flexDirection = FlexDirection.column
            width = 100.pct
        }

        div {
            css {
                fontNormal()
                boxNormal()
                boxShade()
                center()
                justifyContent = JustifyContent.center
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
                        // LOG.debug(uploadImage.toString())
                        scope.launch{
                            val httpResponse = uploadImage(uploadImage)
                            val imageReceived = httpResponse.body<ImageForUploadReceive>()
                            uploadedImageUrl = imageReceived.preview_url
                            imageDropped = imageReceived.toImage()
                            // TODO : send artwork to back-end
                        }
                    }
                }
            }

            imageDropped?.let {
                img {
                    src = uploadedImageUrl
                    // Styles for the product image
                    css {
                        width = 80.px
                        height = 80.px
                        marginRight = 16.px
                    }
                }
            }

            // Title

            // Description


            // Data processing to create the product in Printify store
            imageDropped?.let {

                val placeholder = Placeholder("front", arrayListOf(imageDropped!!))
                val variants = arrayListOf(Variant())
                val print_areas = arrayListOf(
                    PrintArea(
                        variant_ids = variants.map { it.id } as ArrayList<Int>,
                        placeholders = arrayListOf(placeholder)
                    )
                )

                val mugProduct = MugProduct(
                    title = "Default Title",
                    description = "Default Description",
                    variants = variants,
                    print_areas = print_areas
                )

                IconButton{
                    +"Post Dummy Product"
                    PostAdd()
                    onClick = {
                        scope.launch {
                            val httpResponse = postProduct(mugProduct)
                            val productId = httpResponse.body<JsonObject>().get("id").toString().removeSurrounding("\"")

                            if (httpResponse.status != HttpStatusCode.OK){
                                props.onProductCreatedFailed(productId)
                                return@launch
                            }
                            props.onProductCreatedSuccess(productId)

                            publishProduct(productId)

                        }
                    }
                }
            } ?: let {
                div {
                    +"Please upload an image"
                }
            }


        }
    }
}
