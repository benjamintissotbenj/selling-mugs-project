package com.benjtissot.sellingmugs.controllers

import com.benjtissot.sellingmugs.CREATE_PRODUCT_PATH
import com.benjtissot.sellingmugs.PRINTIFY_PATH
import com.benjtissot.sellingmugs.PUBLISH_PRODUCT_PATH
import com.benjtissot.sellingmugs.UPLOAD_IMAGE_PATH
import com.benjtissot.sellingmugs.services.PrintifyService
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
        route(UPLOAD_IMAGE_PATH + "/{public}"){
            post {
                val public : Boolean = call.parameters["public"]?.let {valueOf(it)} ?: error("Invalid public value in post request")
                call.respond(PrintifyService.uploadImage(call.receive(), public))
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
    }
}