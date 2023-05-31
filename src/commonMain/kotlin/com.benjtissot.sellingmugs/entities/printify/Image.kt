package com.benjtissot.sellingmugs.entities.printify

import kotlinx.serialization.Serializable

@Serializable
class Image(val id: String,
            val name: String,
            val type: String,
            val height: Int,
            val width: Int,
            val x: Float,
            val y: Float,
            val scale: Int,
            val angle: Int) {
}