package com.benjtissot.sellingmugs.controllers

import com.benjtissot.sellingmugs.HOMEPAGE_PATH
import com.benjtissot.sellingmugs.apiGenerateStableDiffusionPrompt
import com.benjtissot.sellingmugs.entities.openAI.ChatResponse
import com.benjtissot.sellingmugs.entities.openAI.ChatResponseContent
import com.benjtissot.sellingmugs.entities.openAI.Variation
import io.ktor.client.call.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

fun Route.homepageRouting(){

    route(HOMEPAGE_PATH) {
        get {
            call.respondText(
                this::class.java.classLoader.getResource("static/index.html")!!.readText(),
                ContentType.Text.Html
            )
        }
        post {
            call.respond(HttpStatusCode.OK)
        }
        delete() {
            call.respond(HttpStatusCode.OK)
        }
    }
}