package com.benjtissot.sellingmugs.services

import com.benjtissot.sellingmugs.apiUploadImage
import com.benjtissot.sellingmugs.entities.printify.ImageForUpload
import com.benjtissot.sellingmugs.entities.printify.ImageForUploadReceive
import com.benjtissot.sellingmugs.getUuidFromString
import io.ktor.client.call.*

val LOG = java.util.logging.Logger.getLogger("PrintifyService.kt")

/**
 * Although most of the product handling is done through the printify API directly, we need to
 * keep some trace of the products, artworks etc locally for machine learning purposes (which
 * products work, that sort of thing)
 */
class PrintifyService {
    companion object {

        /**
         * Handles the upload of an image. Stores information about the artwork in the database and uploads the contents to printify
         * @param imageFile the uploaded content
         * @param public determine if the uploaded image should be available publicly
         * @return a [String] containing the URL at which the art can be found
         */
        suspend fun uploadImage(imageFile: ImageForUpload, public: Boolean) : String {
            // Upload to printify and save the resulting Artwork
            val artwork = apiUploadImage(imageFile).body<ImageForUploadReceive>()
                .toArtwork({str -> getUuidFromString(str)},  public)
            ArtworkService.updateArtwork(artwork)
            LOG.info("Artwork was saved")
            return artwork.imageURL
        }

        suspend fun createProduct(){
            // TODO: implement
        }

        suspend fun publishProduct(){
            // TODO: implement
        }
    }
}