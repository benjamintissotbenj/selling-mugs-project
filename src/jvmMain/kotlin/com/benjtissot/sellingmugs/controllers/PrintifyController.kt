package com.benjtissot.sellingmugs.controllers

import com.benjtissot.sellingmugs.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.printifyRouting(){

    route(PRINTIFY_PATH) {
        get {
            call.respond("Hello Printify Controller")
        }
        route(PUBLISH_PRODUCT_PATH){
            post {
                // TODO: publish product to printify
            }
        }
        route(CREATE_PRODUCT_PATH){
            post {
                // TODO: create product for printiry and in database
            }
        }
        route(UPLOAD_IMAGE_PATH){
            post {
                // TODO: upload image to printify and save it in database
            }
        }
    }
}