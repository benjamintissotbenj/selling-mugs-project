package com.benjtissot.sellingmugs.entities.local

import com.benjtissot.sellingmugs.CART_OBJECT_PATH
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Cart(
    @SerialName("_id") val id: String,
    val mugCartItemList: ArrayList<MugCartItem>,
){

    /**
     * Considers two carts are equal if they have the same mugs in the same amount regardless of the order
     */
    override fun equals(other: Any?): Boolean {
        return if (other is Cart) {
            this.mugCartItemList.size == other.mugCartItemList.size &&
                    this.mugCartItemList.map {"${it.mug.id}${it.amount}"}.toSet() == other.mugCartItemList.map {"${it.mug.id}${it.amount}"}.toSet()
        } else {
            super.equals(other)
        }
    }

    override fun toString(): String {
        var returnString = "Cart{\nid=$id, \nmugCartItemList=["
        mugCartItemList.forEach { item ->
            returnString+="\n{mug=${item.mug.id}, amount=${item.amount}},"
        }
        returnString += "\n]\n}"
        return returnString
    }

    fun getTotalCount() : Int {
        return mugCartItemList.sumOf { it.amount }
    }
    companion object {
        // Idea is that we don't need to define a route and requests in strings. Any changes only need to come from the models
        // then the client and the server are adjusted automatically
        const val path = CART_OBJECT_PATH
    }
}