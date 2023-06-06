package com.benjtissot.sellingmugs.entities.printify

import kotlinx.serialization.Serializable

@Serializable
class ImageForUpload(var file_name: String = "", var contents: String = "", var url: String = "") {

    override fun toString() : String{
        return "File for Upload: $file_name with contents $contents or url $url"
    }
}