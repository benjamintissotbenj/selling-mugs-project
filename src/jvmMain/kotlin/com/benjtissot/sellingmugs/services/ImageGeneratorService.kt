package com.benjtissot.sellingmugs.services

import com.benjtissot.sellingmugs.apiGenerateImage
import com.benjtissot.sellingmugs.apiGenerateStableDiffusionPrompt
import com.benjtissot.sellingmugs.entities.openAI.*
import com.benjtissot.sellingmugs.entities.printify.ImageForUpload
import com.benjtissot.sellingmugs.entities.printify.ImageForUploadReceive
import com.benjtissot.sellingmugs.entities.printify.MugProductInfo
import com.benjtissot.sellingmugs.entities.stableDiffusion.ImageResponse
import com.benjtissot.sellingmugs.genUuid
import com.benjtissot.sellingmugs.repositories.ChatRepository
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.logging.*
import kotlinx.datetime.Clock
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.litote.kmongo.json
private val LOG = KtorSimpleLogger("ImageGeneratorService.kt")

class ImageGeneratorService {
    companion object {

        /**
         * Uses OpenAI and Stable Diffusion APIs to create a list of images from a subject
         */
        @Throws
        suspend fun generateImagesFromParams(params: ChatRequestParams) {
            // Get ChatGPT to create a list of variations
            val variations = generateVariationsFromParams(params)

            variations.forEach { variation ->
                // Use StableDiffusion to create an image for each variation
                // TODO: create a name in the variation
                val stableDiffusionImageSource = generateImageFromVariation(variation)

                // Upload generated image to printify
                val imageUploadedToPrintify = uploadImageFromSource("AI Generated", stableDiffusionImageSource)

                imageUploadedToPrintify?.let {
                    // Create mug from image
                    // TODO: description from chatGPT here
                    val mugProductInfo = MugProductInfo("AI Generated - ${it.id}", "", it.toImage())
                    val productPrintifyId = PrintifyService.createProduct(mugProductInfo)
                    productPrintifyId?.let { id ->
                        // Get all generated mug visuals
                        MugService.getMugByPrintifyId(id)?.let { mug ->
                            MugService.updateArtworkImage(mug.artwork, id) // make sure the images are updated when creating the product
                        }
                        // Make product available to buy
                        PrintifyService.publishProduct(id)
                    }
                }

            }
        }

        /**
         * Generates variations from a given subject
         * @param params the [ChatRequestParams] used
         * @return a [List] object that contains [Variation] if the request is a
         * success, and throws an error that contains the appropriate message otherwise.
         */
        @Throws
        private suspend fun generateVariationsFromParams(params: ChatRequestParams) : List<Variation> {
            var apiResponse : HttpResponse
            var exception: Exception?
            var numberOfTries = 0
            val chatRequest = ChatRequest.generateFromParams(params)
            do {
                numberOfTries ++
                LOG.debug("Sending request to API, restarting if Service Unavailable")
                apiResponse = apiGenerateStableDiffusionPrompt(chatRequest)
                exception = null
                if (apiResponse.status == HttpStatusCode.OK){
                    try {
                        val responseObject = apiResponse.body<ChatResponse>()
                        val content = responseObject.choices[0].message.content
                        val chatResponseContent = Json.decodeFromString<ChatResponseContent>(content)

                        insertNewChatLog(chatRequest, chatResponseContent, "Success")

                        return chatResponseContent.variations
                    } catch (e: Exception){
                        exception = e
                        e.printStackTrace()
                    }
                }
                // Runs a maximum of 5 times while there is an exception or unavailable service
            } while (numberOfTries <= 5 && (exception != null || apiResponse.status == HttpStatusCode.ServiceUnavailable))

            val errorMessage = if (exception != null){
                "Exceeded five tries. Last Exception was ${exception.json}"}
            else {
                "Exceeded five tries. Last API response status was ${apiResponse.status}"
            }
            insertNewChatLog(chatRequest, null, errorMessage)
            throw Exception(errorMessage)
        }

        @Throws
        private suspend fun generateImageFromVariation(variation: Variation) : String {
            val httpResponse = apiGenerateImage("${variation.parameters} ${variation.narrative}")
            try {
                val imageResponse = httpResponse.body<ImageResponse>()
                return imageResponse.output[0]
            } catch (e: Exception) {
                e.printStackTrace()
                throw e
            }
        }

        /**
         * Calls [PrintifyService] to upload an image from a fileName and a source url
         */
        private suspend fun uploadImageFromSource(fileName: String, imageSource: String) : ImageForUploadReceive? {
            return PrintifyService.uploadImage(ImageForUpload(file_name = fileName, url = imageSource), true)
        }


        /******************************************
         *
         * ************ Helpers ***************** *
         *
         *****************************************/

        private suspend fun insertNewChatLog(chatRequest: ChatRequest, chatResponseContent: ChatResponseContent?, message: String){
            ChatRepository.insertChatLog(ChatLog(genUuid(), chatRequest, chatResponseContent, message, Clock.System.now()))
        }

    }
}