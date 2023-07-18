package com.benjtissot.sellingmugs

import com.benjtissot.sellingmugs.entities.openAI.ChatRequest
import com.benjtissot.sellingmugs.entities.openAI.ChatRequestParams
import com.benjtissot.sellingmugs.entities.printify.*
import com.benjtissot.sellingmugs.entities.printify.order.*
import com.benjtissot.sellingmugs.entities.stableDiffusion.ImageFetchRequest
import com.benjtissot.sellingmugs.entities.stableDiffusion.ImageRequest
import com.benjtissot.sellingmugs.entities.stableDiffusion.ImageResponse
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
var stableDiffusionClient : HttpClient = HttpClient {
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
        url("https://fast-earth-36264.herokuapp.com/https://stablediffusionapi.com/api/v3/")
        header("origin", "${ConfigConst.HOST}:${ConfigConst.PORT}")  // needed for this to work,
    }
}


/**
 * Generates a stable diffusion image based on the prompt
 */
suspend fun apiGenerateImage(prompt: String) : HttpResponse {
    val imageRequest = ImageRequest.generate(System.getenv("STABLE_DIFFUSION_API_KEY_TEST"), prompt)
    val httpResponse = stableDiffusionClient.post("text2img"){
        contentType(ContentType.Application.Json)
        setBody(
            imageRequest
        )
    }
    return httpResponse
}


/**
 * Generates a stable diffusion image based on the prompt
 */
suspend fun apiFetchImage(id: Int) : HttpResponse {
    val imageRequest = ImageFetchRequest(System.getenv("STABLE_DIFFUSION_API_KEY_TEST"))
    val httpResponse = stableDiffusionClient.post("fetch/$id"){
        contentType(ContentType.Application.Json)
        setBody(
            imageRequest
        )
    }
    return httpResponse
}