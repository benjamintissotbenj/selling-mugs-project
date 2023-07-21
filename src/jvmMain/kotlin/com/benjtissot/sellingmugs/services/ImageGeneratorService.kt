package com.benjtissot.sellingmugs.services

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.entities.local.Category
import com.benjtissot.sellingmugs.entities.openAI.*
import com.benjtissot.sellingmugs.entities.printify.ImageForUpload
import com.benjtissot.sellingmugs.entities.printify.ImageForUploadReceive
import com.benjtissot.sellingmugs.entities.printify.MugProductInfo
import com.benjtissot.sellingmugs.entities.stableDiffusion.ImageGeneratedLog
import com.benjtissot.sellingmugs.entities.stableDiffusion.ImageResponse
import com.benjtissot.sellingmugs.repositories.ChatRepository
import com.benjtissot.sellingmugs.repositories.StableDiffusionRepository
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.logging.*
import kotlinx.coroutines.*
import kotlinx.coroutines.time.delay
import kotlinx.datetime.Clock
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.litote.kmongo.json
import java.io.IOException
import java.lang.Long.max
import java.time.Duration

private val LOG = KtorSimpleLogger("ImageGeneratorService.kt")

class ImageGeneratorService {
    companion object {

        /**
         * Generates categories and mugs for these categories using ChatGPT and Stable Diffusion
         * @param params carries the number of categories you wish to create, number of
         * variations per category and can hold an art type if the user wishes to give one
         * @return the list of status codes to give feedback in the front-end of how well this went
         * @throws [OpenAIUnavailable] when the server is unable to connect with OpenAI's API
         */
        @Throws
        suspend fun generateCategoriesAndMugs(params: CategoriesChatRequestParams) : GenerateCategoriesStatus {
            val generateCategoriesStatusUuid = genUuid()
            val dateSubmitted = Clock.System.now()
            return try {
                val categoriesAndStyle = generateCategories(params.amountOfCategories)
                GenerateCategoriesStatus(
                    generateCategoriesStatusUuid,
                    "Overall success",
                    params,
                    categoriesAndStyle.map { pair ->
                        val catRequestStarted = Clock.System.now()
                        try {
                            val imageType = params.type ?: pair.second
                            val statusCodes = generateMugsFromParams(MugsChatRequestParams(pair.first.name, imageType, params.amountOfVariations))
                            GenerateCategoryStatus(pair.first, "Success", statusCodes, dateSubmitted = catRequestStarted, dateReturned = Clock.System.now())
                        } catch (e: OpenAIUnavailable) {
                            e.printStackTrace()
                            GenerateCategoryStatus(pair.first, e.message, emptyList(), dateSubmitted = catRequestStarted, dateReturned = Clock.System.now())
                        }
                    },
                    dateSubmitted = dateSubmitted,
                    dateReturned = Clock.System.now()
                )
            } catch (e: Exception){
                e.printStackTrace()
                GenerateCategoriesStatus(
                    generateCategoriesStatusUuid,
                    e.message ?: "Something went wrong, consult logs.",
                    params,
                    emptyList(),
                    dateSubmitted = dateSubmitted,
                    dateReturned = Clock.System.now()
                )
            }
        }


        /******************************************
         *
         * ******** Generate Categories ********* *
         *
         *****************************************/

        /**
         * Generates a list of categories and of most appropriate styles to generate images in this category
         * @throws [Exception] if 5 tries are not enough to contact ChatGPT correctly
         */
        private suspend fun generateCategories(amountOfCategories: Int) : List<Pair<Category, Const.StableDiffusionImageType>> {
            val requestCreated = Clock.System.now()
            var apiResponse : HttpResponse
            var exception: Exception?
            var numberOfTries = 0
            val chatRequest = ChatRequest.generateCategoryRequestFromParams(amountOfCategories)
            do {
                numberOfTries ++
                LOG.debug("Sending request to API, restarting if Service Unavailable")
                apiResponse = apiGenerateCategoryList(chatRequest)
                exception = null
                if (apiResponse.status == HttpStatusCode.OK){
                    try {
                        val responseObject = apiResponse.body<ChatResponse>()
                        val content = responseObject.choices[0].message.content // be careful, maybe put a failsafe here if text contains more than just the JSON
                        val chatResponseContent = getCategoriesChatResponseContentFromString(content)

                        insertNewCategoriesChatLog(chatRequest, chatResponseContent, "Success", requestCreated)

                        return chatResponseContent.categories.map { catResponse ->
                            Pair(Category(getUuidFromString(catResponse.category), catResponse.category), catResponse.style)
                        }
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
            insertNewCategoriesChatLog(chatRequest, null, errorMessage, requestCreated)
            throw Exception(errorMessage)
        }



        /******************************************
         *
         * *********** Generate Mugs ************ *
         *
         *****************************************/

        /**
         * Uses OpenAI and Stable Diffusion APIs to create a list of images from a subject
         * @param params the different [MugsChatRequestParams] needed to create a good OpenAI request
         * @return the list of [CustomStatusCode]s associated with each variation, serializable version of [HttpStatusCode]
         * @throws [OpenAIUnavailable] when the server is unable to connect with OpenAI's API
         */
        @OptIn(DelicateCoroutinesApi::class)
        @Throws
        suspend fun generateMugsFromParams(params: MugsChatRequestParams) : List<CustomStatusCode> {
            // Get ChatGPT to create a list of variations
            val variations = try {
                generateVariationsFromParams(params)
            } catch (e: IOException) {
                throw OpenAIUnavailable()
            }
            var counter = 0
            val deferred = variations.map { variation ->
                counter++
                if (counter == 5){
                    delay(1000) // wait 1 second every 5 requests to fit with Stable Diffusion API
                }
                GlobalScope.async {
                    try {
                        // Use StableDiffusion to create an image for each variation
                        val stableDiffusionImageSource = generateImageFromVariation(variation)

                        // Upload generated image to printify
                        val imageUploadedToPrintify =
                            uploadImageFromSource(variation.getCleanName(), stableDiffusionImageSource)

                        // TODO: create function for this, log mug creation status in DB
                        imageUploadedToPrintify?.let {
                            // Create mug from image
                            // TODO: description from chatGPT here
                            val mugProductInfo = MugProductInfo("AI - ${variation.getCleanName()}", "", params.subject, it.toImage())
                            val productPrintifyId = PrintifyService.createProduct(mugProductInfo)
                            productPrintifyId?.let { id ->
                                // Get all generated mug visuals
                                MugService.getMugByPrintifyId(id)?.let { mug ->
                                    MugService.updateArtworkImage(
                                        mug.artwork,
                                        id
                                    ) // make sure the images are updated when creating the product
                                }
                                // Make product available to buy
                                val statusCode = PrintifyService.publishProduct(id)
                                if (statusCode != HttpStatusCode.OK) {
                                    Const.HttpStatusCode_ProductPublicationFailed
                                } else {
                                    statusCode
                                }
                            } ?: Const.HttpStatusCode_ProductCreationFailed
                        } ?: Const.HttpStatusCode_ImageUploadFail

                    } catch (e: Exception) {
                        HttpStatusCode(
                            90,
                            "Error in the process for variation ${variation.name}, message: ${e.message ?: "no-message"}"
                        )
                    }
                }
            }

            return runBlocking {
                LOG.debug("Awaiting coroutines")
                val listOfStatuses = deferred.awaitAll().map { httpStatusCode -> httpStatusCode.toCustom() }
                LOG.debug("All coroutines done :")
                LOG.debug("{")
                listOfStatuses.forEach { status ->
                    LOG.debug(status.print())
                }
                LOG.debug("}")
                listOfStatuses
            }
        }

        /**
         * Generates variations from a given subject
         * @param params the [MugsChatRequestParams] used
         * @return a [List] object that contains [Variation] if the request is a
         * success, and throws an error that contains the appropriate message otherwise.
         */
        @Throws
        private suspend fun generateVariationsFromParams(params: MugsChatRequestParams) : List<Variation> {
            val requestCreated = Clock.System.now()
            var apiResponse : HttpResponse
            var exception: Exception?
            var numberOfTries = 0
            val chatRequest = ChatRequest.generateMugRequestFromParams(params)
            CategoryService.updateCategory(CategoryService.createCategory(params.subject))
            do {
                numberOfTries ++
                LOG.debug("Sending request to API, restarting if Service Unavailable")
                apiResponse = apiGenerateStableDiffusionPrompt(chatRequest)
                exception = null
                if (apiResponse.status == HttpStatusCode.OK){
                    try {
                        val responseObject = apiResponse.body<ChatResponse>()
                        val content = responseObject.choices[0].message.content // be careful, maybe put a failsafe here if text contains more than just the JSON
                        val chatResponseContent = getMugsChatResponseContentFromString(content)

                        insertNewMugsChatLog(chatRequest, chatResponseContent, "Success", requestCreated)

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
            insertNewMugsChatLog(chatRequest, null, errorMessage, requestCreated)
            throw Exception(errorMessage)
        }

        @Throws
        private suspend fun generateImageFromVariation(variation: Variation) : String {
            val requestCreated = Clock.System.now()
            val httpResponse = apiGenerateImage("${variation.parameters} ${variation.narrative}")
            try {
                var imageResponse = httpResponse.body<ImageResponse>()

                while (imageResponse.status == "processing"){
                    val delay = max(imageResponse.eta.toLong(), 5L) // at least 5 seconds
                    LOG.debug("Variation ${variation.name} is queued, eta $delay seconds")
                    // If we are still processing, delay for given eta and
                    delay(Duration.ofSeconds(delay))
                    LOG.debug("Fetching variation ${variation.name}")
                    val httpResponseFetch = apiFetchImage(imageResponse.id)
                    imageResponse = httpResponseFetch.body()
                }

                return if (imageResponse.output.isEmpty()){
                        insertNewImageGeneratedLog(variation, "", "Fetch source is empty", requestCreated)
                        throw Exception("Fetch source is empty")
                    } else {
                    insertNewImageGeneratedLog(variation, imageResponse.output[0], "", requestCreated)
                        imageResponse.output[0]
                    }

            } catch (e: Exception) {
                e.printStackTrace()
                insertNewImageGeneratedLog(variation, "", e.message ?: "Unknown error", requestCreated)
                throw e
            }
        }

        /**
         * Calls [PrintifyService] to upload an image from a fileName and a source url
         */
        private suspend fun uploadImageFromSource(fileName: String, imageSource: String) : ImageForUploadReceive? {
            var imageForUploadReceive : ImageForUploadReceive? = null
            var nbTries = 0
            while (imageForUploadReceive == null && nbTries < 5){
                nbTries ++
                imageForUploadReceive = PrintifyService.uploadImage(ImageForUpload(file_name = fileName, url = imageSource), true)
                if (imageForUploadReceive == null){
                    delay(1000L) // wait 1 s if image does not upload immediately correctly
                }
            }
            return imageForUploadReceive
        }

        /**
         * Gets a clean version of the chat responseContent, even if the chat response has text before or after the JSON
         */
        private fun getMugsChatResponseContentFromString(content: String): MugsChatResponseContent {
            return try {
                Json.decodeFromString(content)
            } catch (e: Exception){
                // Most likely, the content has more than simply the json in it
                val pureJsonContent = content.substringAfter("{","").substringBeforeLast("}","")
                Json.decodeFromString("{$pureJsonContent}")
            }
        }

        /**
         * Gets a clean version of the chat responseContent, even if the chat response has text before or after the JSON
         */
        private fun getCategoriesChatResponseContentFromString(content: String): CategoriesChatResponseContent {
            return try {
                Json.decodeFromString(content)
            } catch (e: Exception){
                // Most likely, the content has more than simply the json in it
                val pureJsonContent = content.substringAfter("{","").substringBeforeLast("}","")
                Json.decodeFromString("{$pureJsonContent}")
            }
        }



        /******************************************
         *
         * ************ Helpers ***************** *
         *
         *****************************************/

        private suspend fun insertNewMugsChatLog(chatRequest: ChatRequest, mugsChatResponseContent: MugsChatResponseContent?, message: String, requestCreated: kotlinx.datetime.Instant){
            ChatRepository.insertChatLog(ChatLog(genUuid(), chatRequest, mugsChatResponseContent, null, message, requestSubmitted = requestCreated, Clock.System.now()))
        }

        private suspend fun insertNewCategoriesChatLog(chatRequest: ChatRequest, categoriesChatResponseContent: CategoriesChatResponseContent?, message: String, requestCreated: kotlinx.datetime.Instant){
            ChatRepository.insertChatLog(ChatLog(genUuid(), chatRequest, null, categoriesChatResponseContent, message, requestSubmitted = requestCreated, Clock.System.now()))
        }

        private suspend fun insertNewImageGeneratedLog(variation: Variation, imageURL: String, message: String, requestCreated: kotlinx.datetime.Instant){
            StableDiffusionRepository.insertImageGeneratedLog(ImageGeneratedLog(genUuid(), variation, imageURL, message, requestSubmitted = requestCreated, Clock.System.now()))
        }

    }
}