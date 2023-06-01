package com.benjtissot.sellingmugs.components

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.entities.printify.*
import csstype.Display
import csstype.FlexDirection
import csstype.JustifyContent
import csstype.pct
import emotion.react.css
import io.ktor.client.call.*
import io.ktor.http.*
import io.ktor.util.logging.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import mui.icons.material.PostAdd
import mui.material.IconButton
import react.FC
import react.dom.html.ReactHTML.div


private val LOG = KtorSimpleLogger("CreateProductComponent.kt")

external interface CreateProductProps : NavigationProps {
    var onProductCreatedSuccess : (productId: String) -> Unit
    var onProductCreatedFailed : (productId: String) -> Unit
}

val CreateProductComponent = FC<CreateProductProps> { props ->
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
                center()
                justifyContent = JustifyContent.center
            }

            // Title

            // Description

            val image = Image("6477219c4667a70918608ee5", "Default Image", "image/jpg", 1000, 1000, 0.5f, 0.5f, 1, 0)
            val placeholder = Placeholder("front", arrayListOf(image))
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


        }
    }
}
