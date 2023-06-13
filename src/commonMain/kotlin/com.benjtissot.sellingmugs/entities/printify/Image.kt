package com.benjtissot.sellingmugs.entities.printify

import com.benjtissot.sellingmugs.entities.Artwork
import kotlinx.serialization.Serializable

@Serializable
class Image(val id: String,
            val name: String,
            val type: String,
            val height: Int,
            val width: Int,
            val x: Float = 0.5f,
            val y: Float = 0.5f,
            val scale: Float = 1f, // this means scale relative to width
            val angle: Int = 0) {
}
@Serializable
class ImageForUploadReceive(val id: String,
            val file_name: String,
            val mime_type: String,
            val height: Int,
            val width: Int,
            val size: Int,
            val preview_url: String,
            val upload_time: String) {

    fun toImage() : Image {
        return Image(id, file_name, mime_type, height, width)
    }

    /**
     * Creates an artwork based on the received image, and retrieves the correct artwork UUID with a given method
     * @param uuidGen a method to retrieve a [UUID] [String] from the printify store id.
     * @param public determines if the artwork should be public or not. Get it from the HTTP request
     */
    fun toArtwork(uuidGen: (String) -> String, public: Boolean = true) : Artwork {
        return Artwork(uuidGen(id), id, preview_url, public)
    }
    
    override fun toString() : String {
        return "ImageForUploadReceive[$id, $file_name, $mime_type, $height, $width, $size, $preview_url, $upload_time]"
    }

}

@Serializable
class ImageForUpload(var file_name: String = "", var contents: String = "", var url: String = "") {

    override fun toString() : String{
        return "File for Upload: $file_name with contents $contents or url $url"
    }
}

@Serializable
class ImageForProduct(
    var src: String,
    var variant_ids: List<String>,
    var position: String,
    var is_default: Boolean,
    var is_selected_for_publishing: Boolean
    ) {
}