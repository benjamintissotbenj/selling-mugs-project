package com.benjtissot.sellingmugs

import com.benjtissot.sellingmugs.entities.openAI.ChatRequest
import com.benjtissot.sellingmugs.entities.openAI.ChatRequestParams
import com.benjtissot.sellingmugs.entities.printify.*
import com.benjtissot.sellingmugs.entities.printify.order.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

/**
 * Back-end owned OPEN AI API
 */
var openAIClient : HttpClient = HttpClient {
    install(ContentNegotiation) {
        json(Json{ ignoreUnknownKeys = true })
    }
    install(Logging){
        level = LogLevel.BODY
        filter { _ ->
            true
        }
    }
    defaultRequest {
        url("https://fast-earth-36264.herokuapp.com/https://api.openai.com/v1/")
        header("Authorization", "Bearer ${System.getenv("OPENAI_API_KEY_TEST")}")
        header("origin", "${ConfigConst.HOST}:${ConfigConst.PORT}")  // needed for this to work,
    }
}


/**
 * Generates a stable diffusion prompt on the subject of the given parameter
 */
suspend fun apiGenerateStableDiffusionPrompt(params: ChatRequestParams) : HttpResponse {
    return openAIClient.post("chat/completions"){
        contentType(ContentType.Application.Json)
        setBody(
            ChatRequest.generateFromParams(params)
        )
    }
}