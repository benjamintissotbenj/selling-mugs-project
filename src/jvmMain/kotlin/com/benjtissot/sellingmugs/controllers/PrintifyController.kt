package com.benjtissot.sellingmugs.controllers

import com.benjtissot.sellingmugs.*
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
                // TODO: upload image to printify and save it in database
                call.respondText(PrintifyService.uploadImage(call.receive(), public))
            }
        }
        route(CREATE_PRODUCT_PATH){
            post {
                // TODO: create product for printiry and in database
            }
        }
        route(PUBLISH_PRODUCT_PATH){
            post {
                // TODO: publish product to printify
            }
        }
    }
}