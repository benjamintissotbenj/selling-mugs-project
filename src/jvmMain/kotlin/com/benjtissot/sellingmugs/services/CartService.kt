package com.benjtissot.sellingmugs.services

import com.benjtissot.sellingmugs.entities.local.MugCartItem
import com.benjtissot.sellingmugs.entities.local.Cart
import com.benjtissot.sellingmugs.entities.local.Mug
import com.benjtissot.sellingmugs.entities.local.Session
import com.benjtissot.sellingmugs.genUuid
import com.benjtissot.sellingmugs.repositories.CartRepository
import com.benjtissot.sellingmugs.repositories.SessionRepository
import com.benjtissot.sellingmugs.repositories.UserRepository
import io.ktor.util.logging.*


private val LOG = KtorSimpleLogger("CartService.kt")
class CartService {
    companion object {

        suspend fun getCart(id: String) : Cart? {
            return CartRepository.getCart(id)
        }

        suspend fun getCart(session: Session) : Cart {
            return CartRepository.getCart(session.cartId) ?: CartRepository.createCart().also { SessionRepository.updateSession(
                session.copy(cartId = it.id)
            ) }
        }

        @Throws
        suspend fun addMugToCart(mug: Mug, cart: Cart) {
            cart.mugCartItemList.find { it.mug.id == mug.id }?.also {
                it.amount++
            } ?: run {
                cart.mugCartItemList.add(MugCartItem(mug, 1))
            }
            CartRepository.updateCart(cart)
        }

        @Throws
        suspend fun removeMugCartItemFromCart(mugCartItem: MugCartItem, cart: Cart) {
            cart.mugCartItemList.remove(mugCartItem)
            CartRepository.updateCart(cart)
        }

        /**
         * Changes the quantity of a mugCartItem. If the total quantity hits zero, removes the item from the cart
         */
        suspend fun changeMugCartItemQuantity(cart: Cart, mugCartItem: MugCartItem, deltaQuantity: Int) {
            val totalAmount = mugCartItem.amount + deltaQuantity
            if (totalAmount == 0) {
                removeMugCartItemFromCart(mugCartItem, cart)
            } else {
                cart.mugCartItemList.find{ it.mug.id == mugCartItem.mug.id }?.amount = totalAmount
                CartRepository.updateCart(cart)
            }
        }

        /**
         * Creates a copy of the cart identified by cartId and saves that copied cart id
         * into a [User] object to be able to retrieve that cart later.
         */
        suspend fun saveCartToUser(session: Session, userId: String) : Session? {
            val newUuid = genUuid()
            CartRepository.getCart(session.cartId)?.let { CartRepository.insertCart(it.copy(id = newUuid)) }
            return UserRepository.getUserById(userId)?.copy(savedCartId = newUuid)?.let { user ->
                SessionRepository.updateSession(session.copy(user = UserRepository.updateUser(user) ))
            }
        }

        /**
         * If all the conditions are united, loads the user's saved cart id into the session's active cart id
         */
        suspend fun loadCartIdIntoSession(session: Session) : Boolean {
            var success = false
            // Updates the current cart with the saved cart items
            val currentCart = CartRepository.getCart(session.cartId)
            session.user?.savedCartId?.let {
                val cartToLoad = CartRepository.getCart(it)
                cartToLoad?.let { savedCart ->
                    currentCart?.copy(mugCartItemList = savedCart.mugCartItemList)?.let { copiedCart ->
                        LOG.debug("Loading cart : $copiedCart")
                        CartRepository.updateCart(copiedCart)
                        success = true
                    }
                }?: run {
                    LOG.debug("Can't find the cart to load with id $it")
                }
            } ?: run {
                LOG.debug("Can't find the saved cart id")
            }
            return success
        }
    }
}