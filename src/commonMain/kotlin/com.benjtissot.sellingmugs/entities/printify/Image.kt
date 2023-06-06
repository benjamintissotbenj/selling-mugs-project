package com.benjtissot.sellingmugs.entities.printify

import kotlinx.serialization.Serializable

@Serializable
class Image(val id: String,
            val name: String,
            val type: String,
            val height: Int,
            val width: Int,
            val x: Float = 0.5f,
            val y: Float = 0.5f,
            val scale: Int = 1,
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
}