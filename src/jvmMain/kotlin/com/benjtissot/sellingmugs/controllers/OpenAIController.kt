package com.benjtissot.sellingmugs.controllers

import com.benjtissot.sellingmugs.OPEN_AI_PATH
import com.benjtissot.sellingmugs.entities.openAI.ChatRequestParams
import com.benjtissot.sellingmugs.services.ImageGeneratorService
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.logging.*

private val LOG = KtorSimpleLogger("OpenAIController.kt")

fun Route.openAIRouting(){

    route(OPEN_AI_PATH) {
        post {
            val params: ChatRequestParams = call.receive()
            try {
                ImageGeneratorService.generateImagesFromParams(params)
                call.respond(HttpStatusCode.OK)
            } catch (e: Exception) {
                e.printStackTrace()
                call.respond(HttpStatusCode.InternalServerError)
            }
        }
    }
}