package com.benjtissot.sellingmugs.repositories

import com.benjtissot.sellingmugs.entities.Cart
import com.benjtissot.sellingmugs.genUuid
import database
import org.litote.kmongo.MongoOperator
import org.litote.kmongo.and
import org.litote.kmongo.eq
import org.litote.kmongo.upsert

val cartCollection = database.getCollection<Cart>()

class CartRepository {
    companion object {

        /**
         * Creates a [Cart] in the database
         * @return the created cart
         */
        suspend fun createCart() : Cart {
            val newCart = Cart(genUuid(), arrayListOf())
            cartCollection.insertOne(newCart)
            return newCart
        }

        suspend fun getCart(id: String) : Cart? {
            return cartCollection.findOne(Cart::id eq id)
        }

        /**
         * @param cart the [Cart] to be inserted
         */
        suspend fun insertCart(cart: Cart) {
            cartCollection.insertOne(cart)
        }

        /**
         * @param cart the [Cart] to be updated (inserted if not existent)
         */
        suspend fun updateCart(cart: Cart) {
            cartCollection.updateOneById(cart.id, cart, upsert())
        }

    }
}