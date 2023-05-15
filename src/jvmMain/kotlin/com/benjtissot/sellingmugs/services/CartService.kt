package com.benjtissot.sellingmugs.services

import com.benjtissot.sellingmugs.MugCartItem
import com.benjtissot.sellingmugs.entities.Cart
import com.benjtissot.sellingmugs.entities.Mug
import com.benjtissot.sellingmugs.genUuid
import com.benjtissot.sellingmugs.repositories.CartRepository
import com.benjtissot.sellingmugs.services.SessionService.Companion.updateCartIdInSession
import io.ktor.server.application.*
import io.ktor.util.pipeline.*

class CartService {
    companion object {

        suspend fun PipelineContext<*, ApplicationCall>.getCart(id: String) : Cart {
            return CartRepository.getCart(id) ?: CartRepository.createCart().also {
                updateCartIdInSession(it.id)
            }
        }

        @Throws
        suspend fun addMugToCart(mug: Mug, cart: Cart) {
            cart.mugCartItemList.find { it.mug.id == mug.id }?.also {
                it.amount++
            } ?: run {
                cart.mugCartItemList.add(MugCartItem(genUuid().toString(), mug, 1))
            }
            CartRepository.updateCart(cart)
        }
    }
}