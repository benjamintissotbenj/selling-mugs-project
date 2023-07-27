package com.benjtissot.sellingmugs.services

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.entities.local.Category
import com.benjtissot.sellingmugs.entities.openAI.*
import com.benjtissot.sellingmugs.entities.printify.ImageForUpload
import com.benjtissot.sellingmugs.entities.printify.ImageForUploadReceive
import com.benjtissot.sellingmugs.entities.printify.MugProductInfo
import com.benjtissot.sellingmugs.entities.printify.ProductLog
import com.benjtissot.sellingmugs.entities.stableDiffusion.ImageGeneratedLog
import com.benjtissot.sellingmugs.entities.stableDiffusion.ImageResponse
import com.benjtissot.sellingmugs.repositories.ChatRepository
import com.benjtissot.sellingmugs.repositories.MugRepository
import com.benjtissot.sellingmugs.repositories.StableDiffusionRepository
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.logging.*
import kotlinx.coroutines.*
import kotlinx.coroutines.time.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
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
        @OptIn(DelicateCoroutinesApi::class)
        @Throws
        suspend fun generateCategoriesAndMugs(params: CategoriesChatRequestParams) : GenerateCategoriesStatus {
            val generateCategoriesStatusUuid = genUuid()
            val dateSubmitted = Clock.System.now()
            return try {
                val categoriesAndStyle = generateCategories(params.amountOfCategories, params.newCategoriesOnly)
                // Using coroutines to parallelize the work
                val deferred = categoriesAndStyle.map { pair ->
                    GlobalScope.async {
                        val catRequestStarted = Clock.System.now()
                        try {
                            val imageType = params.type ?: pair.second
                            val statusCodes = generateMugsFromParams(MugsChatRequestParams(pair.first.name, imageType, params.amountOfVariations))
                            GenerateCategoryStatus(pair.first, "Success", statusCodes, dateSubmitted = catRequestStarted, dateReturned = Clock.System.now())
                        } catch (e: OpenAIUnavailable) {
                            e.printStackTrace()
                            GenerateCategoryStatus(pair.first, e.message, emptyList(), dateSubmitted = catRequestStarted, dateReturned = Clock.System.now())
                        }
                    }
                }
                GenerateCategoriesStatus(
                    generateCategoriesStatusUuid,
                    "Overall success",
                    params,
                    deferred.awaitAll(),
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
        private suspend fun generateCategories(amountOfCategories: Int, newCategoriesOnly: Boolean) : List<Pair<Category, Const.StableDiffusionImageType>> {
            val requestCreated = Clock.System.now()
            var apiResponse : HttpResponse
            var exception: Exception?
            var numberOfTries = 0

            val chatRequest = ChatRequest.generateCategoryRequestFromParams(
                amountOfCategories,
                if (newCategoriesOnly) {
                    CategoryService.getAllCategories()
                } else {
                    emptyList()
                }
            )
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
         * Generates a single design from the given parameters and returns the result of it's upload to printify
         * @param params the variation name and the style in which to draw it
         * @return [ImageForUploadReceive] when the upload is a success, null when we have tried 5 times without succeeding
         * @throws [OpenAIUnavailable] when the service takes too long to reply
         * @throws [Exception] when there is an unknown error
         */
        suspend fun generateDesignFromParams(params: MugsChatRequestParams) : ImageForUploadReceive? {
            // Get ChatGPT to create a list of variations
            val variations = try {
                generateVariationsFromParams(params)
            } catch (e: IOException) {
                throw OpenAIUnavailable()
            }
            if (variations.isEmpty()) {
                throw OpenAIUnavailable()
            }
            val variation = variations[0]
            return try {
                // Use StableDiffusion to create an image for each variation
                val stableDiffusionImageSource = generateImageFromVariation(variation)

                // Upload generated image to printify
                uploadImageFromSource(variation.getCleanName(), stableDiffusionImageSource)
            } catch (e: Exception) {
                throw Exception("Error in the process for variation ${variation.name}, message: ${e.message ?: "no-message"}")
            }
        }

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

                        imageUploadedToPrintify?.let {
                            publishMugFromImage(it, variation, params.subject)
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
         * @throws [Exception] when the variation could not be created (if we tried more than 5 times without
         * success, or if a chat response had the wrong format
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

        /**
         * Generates an image using Stable Diffusion for a given variation
         * @param variation the [Variation] to create an image from
         * @return a [String] representing the URL of the generated image
         * @throws [Exception] when the image could not be created
         */
        @Throws
        private suspend fun generateImageFromVariation(variation: Variation) : String {
            val requestCreated = Clock.System.now()
            val httpResponse = apiGenerateImage("${variation.parameters} ${variation.narrative}", variation.negativePrompt)
            try {
                var imageResponse = httpResponse.body<ImageResponse>()
                var attempts = 0

                while (imageResponse.status == "processing" && attempts < 5){
                    val delay = max(imageResponse.eta.toLong(), 5L) // at least 5 seconds
                    LOG.debug("Variation ${variation.name} is queued, eta $delay seconds")
                    // If we are still processing, delay for given eta and
                    delay(Duration.ofSeconds(delay))
                    LOG.debug("Fetching variation ${variation.name}")
                    // If imageResponse.id is null, try again without simply trying to fetch the image
                    imageResponse.id?.let {
                        val httpResponseFetch = apiFetchImage(imageResponse.id!!)
                        imageResponse = httpResponseFetch.body()
                    }
                    attempts++
                }

                return if (imageResponse.output.isNullOrEmpty()){
                        insertNewImageGeneratedLog(variation, "", "Fetch source is empty, tried to fetch $attempts times", requestCreated)
                        throw Exception("Fetch source is empty")
                    } else {
                    insertNewImageGeneratedLog(variation, imageResponse.output!![0], "", requestCreated)
                        imageResponse.output!![0]
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
         * Creates and publishes a Mug to Printify
         * @param imageForUpload the [ImageForUploadReceive] received by Printify's API
         * @param variation the [Variation] created by chatGPT
         * @param categoryName the name of the category to create the [MugProductInfo]
         */
        private suspend fun publishMugFromImage(imageForUpload: ImageForUploadReceive, variation: Variation, categoryName: String) : HttpStatusCode {
            val dateCreatedLocally = Clock.System.now()
            // Create mug from image
            val mugProductInfo = MugProductInfo(
                title = variation.getCleanName(),
                description = "AI generated : ${variation.description}",
                categoryName = categoryName,
                image = imageForUpload.toImage(),
                fullPrompt = variation.narrative
            )
            val productPrintifyId = PrintifyService.createProduct(mugProductInfo)

            return if (productPrintifyId == null) {
                insertNewProductLog("", Const.HttpStatusCode_ProductCreationFailed.description, dateCreatedLocally, null, null)
                Const.HttpStatusCode_ProductCreationFailed
            } else {
                val dateProductCreated = Clock.System.now()
                // Get all generated mug visuals
                MugService.getMugByPrintifyId(productPrintifyId)?.let { mug ->
                    MugService.updateArtworkImage(
                        mug.artwork,
                        productPrintifyId
                    ) // make sure the images are updated when creating the product
                }
                // Make product available to buy
                val statusCode = PrintifyService.publishProduct(productPrintifyId)
                if (statusCode != HttpStatusCode.OK) {
                    insertNewProductLog(productPrintifyId, Const.HttpStatusCode_ProductPublicationFailed.description, dateCreatedLocally, dateProductCreated, null)
                    Const.HttpStatusCode_ProductPublicationFailed
                } else {
                    insertNewProductLog(productPrintifyId, "Success", dateCreatedLocally, dateProductCreated, Clock.System.now())
                    statusCode
                }
            }
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

        private suspend fun insertNewMugsChatLog(chatRequest: ChatRequest, mugsChatResponseContent: MugsChatResponseContent?, message: String, requestCreated: Instant){
            ChatRepository.insertChatLog(ChatLog(genUuid(), chatRequest, mugsChatResponseContent, null, message, requestSubmitted = requestCreated, Clock.System.now()))
        }

        private suspend fun insertNewCategoriesChatLog(chatRequest: ChatRequest, categoriesChatResponseContent: CategoriesChatResponseContent?, message: String, requestCreated: Instant){
            ChatRepository.insertChatLog(ChatLog(genUuid(), chatRequest, null, categoriesChatResponseContent, message, requestSubmitted = requestCreated, Clock.System.now()))
        }

        private suspend fun insertNewImageGeneratedLog(variation: Variation, imageURL: String, message: String, requestCreated: Instant){
            StableDiffusionRepository.insertImageGeneratedLog(ImageGeneratedLog(genUuid(), variation, imageURL, message, requestSubmitted = requestCreated, Clock.System.now()))
        }

        private suspend fun insertNewProductLog(printifyId: String, message: String, dateCreatedLocally: Instant, dateProductCreated: Instant?, dateProductPublished: Instant?){
            MugRepository.insertProductLog(ProductLog(genUuid(), printifyId, message, dateCreatedLocally, dateProductCreated, dateProductPublished))
        }

    }
}