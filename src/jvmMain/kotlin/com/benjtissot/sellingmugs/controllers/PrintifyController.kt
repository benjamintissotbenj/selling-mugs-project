package com.benjtissot.sellingmugs.controllers

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.entities.printify.UpdateProductImage
import com.benjtissot.sellingmugs.entities.printify.UpdateProductTitleDesc
import com.benjtissot.sellingmugs.services.PrintifyService
import com.benjtissot.sellingmugs.services.PrintifyService.Companion.getProductPreviewImages
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.logging.*
import java.lang.Boolean.valueOf

private val LOG = KtorSimpleLogger("PrintifyController.kt")

fun Route.printifyRouting(){

    route(PRINTIFY_PATH) {
        get {
            call.respond("Hello Printify Controller")
        }
        route("$UPLOAD_IMAGE_PATH/{public}"){
            post {
                val public : Boolean = call.parameters["public"]?.let {valueOf(it)} ?: error("Invalid public value in post request")
                val imageForUploadReceive = PrintifyService.uploadImage(call.receive(), public)
                imageForUploadReceive?.let{
                    call.respond(imageForUploadReceive)
                } ?: let {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
        }
        route(CREATE_PRODUCT_PATH){
            post {
                val productId = PrintifyService.createProduct(call.receive())
                productId?.let{
                    call.respond(productId)
                } ?: let {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
        }
        route(PUBLISH_PRODUCT_PATH){
            post {
                call.respond(PrintifyService.publishProduct(call.receive()))
            }
        }

        route("$PRODUCT_PATH/{productId}"){
            get {
                val productId : String = call.parameters["productId"] ?: error("Invalid public value in post request")
                PrintifyService.getProduct(productId)?.let {
                    call.respond(it)
                } ?: let {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }

            put {
                try {
                    val productId: String =
                        call.parameters["productId"] ?: error("No productId value in update product put request")
                    LOG.debug("Putting an update with update type ${call.request.queryParameters[Const.updateType]}")
                    if (!call.request.queryParameters[Const.updateType].isNullOrBlank()) {
                        when (call.request.queryParameters[Const.updateType]) {
                            Const.titleDesc -> { PrintifyService.putProductTitleDesc(productId, call.receive())?.let {
                                    call.respond(it)
                                } ?: let {
                                    call.respond(HttpStatusCode.BadRequest)
                                }
                            }

                            Const.image -> { PrintifyService.putProductImage(productId, call.receive())?.let {
                                    call.respond(it)
                                } ?: let {
                                    call.respond(HttpStatusCode.BadRequest)
                                }
                            }

                            else -> call.respond(HttpStatusCode.BadRequest)
                        }
                    } else {
                        call.respond(HttpStatusCode(400, "No updateType value in update product put request"))
                    }
                } catch (e : Exception) {
                    e.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }

            route(IMAGES_PATH){
                get {
                    val productId : String = call.parameters["productId"] ?: error("Invalid public value in post request")
                    call.respond(getProductPreviewImages(productId))
                }
            }
        }
    }
}