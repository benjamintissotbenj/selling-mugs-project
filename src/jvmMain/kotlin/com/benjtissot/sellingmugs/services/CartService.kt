package com.benjtissot.sellingmugs.services

import com.benjtissot.sellingmugs.MugCartItem
import com.benjtissot.sellingmugs.entities.Cart
import com.benjtissot.sellingmugs.entities.Mug
import com.benjtissot.sellingmugs.genUuid
import com.benjtissot.sellingmugs.repositories.CartRepository

class CartService {
    companion object {

        suspend fun getCart(id: String) : Cart? {
            return CartRepository.getCart(id)
        }

        @Throws
        suspend fun addMugToCart(mug: Mug, cart: Cart) {
            cart.mugCartItemList.find { it.mug.id == mug.id }?.also {
                it.amount++
            } ?: run {
                cart.mugCartItemList.add(MugCartItem(genUuid(), mug, 1))
            }
            CartRepository.updateCart(cart)
        }

        @Throws
        suspend fun removeMugCartItemFromCart(mugCartItem: MugCartItem, cart: Cart) {
            cart.mugCartItemList.remove(mugCartItem)
            CartRepository.updateCart(cart)
        }
    }
}