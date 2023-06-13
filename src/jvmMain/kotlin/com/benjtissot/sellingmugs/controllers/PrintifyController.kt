package com.benjtissot.sellingmugs.controllers

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.entities.printify.ReceiveProduct
import com.benjtissot.sellingmugs.services.PrintifyService
import com.benjtissot.sellingmugs.services.PrintifyService.Companion.getProductPreviewImages
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.lang.Boolean.valueOf

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
            route(IMAGES_PATH){
                get {
                    val productId : String = call.parameters["productId"] ?: error("Invalid public value in post request")
                    call.respond(getProductPreviewImages(productId))
                }
            }
        }
    }
}