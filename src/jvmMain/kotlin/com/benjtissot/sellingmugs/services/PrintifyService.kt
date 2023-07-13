package com.benjtissot.sellingmugs.services

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.entities.Mug
import com.benjtissot.sellingmugs.entities.printify.*
import com.benjtissot.sellingmugs.repositories.MugRepository
import io.ktor.client.call.*
import io.ktor.http.*
import io.ktor.util.logging.*
import kotlinx.serialization.json.JsonObject


/**
 * Although most of the product handling is done through the printify API directly, we need to
 * keep some trace of the products, artworks etc locally for machine learning purposes (which
 * products work, that sort of thing)
 */
class PrintifyService {
    companion object {

        val LOG = KtorSimpleLogger("PrintifyService.kt")

        /**
         * Handles the upload of an image. Stores information about the artwork in the database and uploads the contents to printify
         * @param imageFile the uploaded content
         * @param public determine if the uploaded image should be available publicly
         * @return a [String] containing the URL at which the art can be found
         */
        suspend fun uploadImage(imageFile: ImageForUpload, public: Boolean) : ImageForUploadReceive? {
            // Upload to printify and save the resulting Artwork
            val httpResponse = apiUploadImage(imageFile)
            if (httpResponse.status != HttpStatusCode.OK){
                return null
            }
            val receivedImage = httpResponse.body<ImageForUploadReceive>()

            ArtworkService.updateArtwork(receivedImage.toArtwork({str -> getUuidFromString(str)},  public))
            return receivedImage
        }

        /**
         * @return the printify product id
         */
        suspend fun createProduct(mugProductInfo: MugProductInfo) : String? {
            val mugProduct = mugProductInfo.toMugProduct()
            val httpResponse = apiCreateProduct(mugProduct)
            if (httpResponse.status != HttpStatusCode.OK){
                return null
            }
            // Now that we have uploaded the MugProduct, we can save a Mug to our database
            val productId = httpResponse.body<JsonObject>().get("id").toString().removeSurrounding("\"")
            val artwork = ArtworkService.findArtworkByPrintifyId(mugProductInfo.image.id)
            artwork?.let {
                // Update the artwork and the mug with the preview images from printify
                if (artwork.previewURLs.isEmpty()) {
                    MugService.updateArtworkImage(artwork, productId)
                }

                val mug = Mug(getUuidFromString(productId), productId, mugProduct.title, mugProduct.description, mugProduct.variants[0].price/100f, artwork)
                MugRepository.updateMug(mug)
                return productId
            } ?: let {
                return null
            }
        }

        /**
         * @param productId Publishes the product created under productId
         *
         */
        suspend fun publishProduct(productId: String) : HttpStatusCode {
            val publishStatus = apiPublishProduct(productId)
            val publishSuccessStatus = apiPublishingSuccessfulProduct(productId)
            if (publishStatus == HttpStatusCode.OK && publishSuccessStatus == HttpStatusCode.OK){
                return HttpStatusCode.OK
            } else {
                LOG.error("Product could not be published")
                return HttpStatusCode.InternalServerError
            }
        }

        suspend fun deleteProduct(productId: String) : HttpStatusCode {
            val statusCode = apiDeleteProduct(productId).status
            if (statusCode == HttpStatusCode.OK){
                MugService.deleteMugByPrintifyId(productId)
            }
            return statusCode
        }


        /**
         * Gets a product from the store
         * @param productId the printify id of the product to get
         * @return a [ReceiveProduct] object that holds all the information concerning the product
         */
        suspend fun getProduct(productId: String): ReceiveProduct? {
            val httpResponse = apiGetProduct(productId)
            return if (httpResponse.status == HttpStatusCode.OK){
                httpResponse.body<ReceiveProduct>()
            } else {
                null
            }
        }


        /**
         * Updates a product's image from the store
         * @param productId the printify id of the product to get
         * @param updatedProductImage the product image to be updated
         * @return a [ReceiveProduct] object that holds all the information concerning the product
         */
        suspend fun putProductImage(productId: String, updatedProductImage: UpdateProductImage): ReceiveProduct? {
            val httpResponse = apiUpdateProductImage(productId, updatedProductImage)
            return if (httpResponse.status == HttpStatusCode.OK){
                MugService.getMugByPrintifyId(productId)?.artwork?.let {
                    MugService.updateArtworkImage(it, productId)
                }
                httpResponse.body()
            } else {
                null
            }
        }


        /**
         * Updates a product from the store
         * @param productId the printify id of the product to get
         * @param updatedProductTitleDesc the product title and description to be updated
         * @return a [ReceiveProduct] object that holds all the information concerning the product
         */
        suspend fun putProductTitleDesc(productId: String,  updatedProductTitleDesc: UpdateProductTitleDesc): ReceiveProduct? {
            // Prepend the title with "Test" whenever not in production
            val prependedUpdatedTitleDesc = if (System.getenv(Const.IS_PRODUCTION_STRING)?.toBoolean() != true) { // includes null
                updatedProductTitleDesc.copy(title = "Test ${updatedProductTitleDesc.title}")
            } else {updatedProductTitleDesc}
            val httpResponse = apiUpdateProductTitleDesc(productId, prependedUpdatedTitleDesc)
            LOG.debug("Printify API response : $httpResponse")
            return if (httpResponse.status == HttpStatusCode.OK){
                MugService.getMugByPrintifyId(productId)?.let {
                    val updatedMug = MugRepository.updateMug(it.copy(name = prependedUpdatedTitleDesc.title, description = prependedUpdatedTitleDesc.description))
                    LOG.debug("Updated local mug is ${updatedMug.name}")
                }
                httpResponse.body()
            } else {
                null
            }
        }


        /**
         * Gets the preview images sources for a product from the store
         * @param printifyProductId the printify id of the product to get
         * @return a [List]<[String]> object that holds all the preview images for the product
         */
        suspend fun getProductPreviewImages(printifyProductId: String): List<String> {
            val httpResponse = apiGetProduct(printifyProductId)
            return if (httpResponse.status == HttpStatusCode.OK){
                val receiveProduct = httpResponse.body<ReceiveProduct>()
                receiveProduct.images.map { img -> img.src }
            } else {
                emptyList()
            }
        }
    }
}