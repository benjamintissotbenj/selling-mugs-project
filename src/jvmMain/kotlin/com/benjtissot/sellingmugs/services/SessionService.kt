package com.benjtissot.sellingmugs.services

import com.benjtissot.sellingmugs.entities.local.Session
import com.benjtissot.sellingmugs.repositories.SessionRepository
import io.ktor.server.application.*
import io.ktor.server.sessions.*
import io.ktor.util.pipeline.*

class SessionService {
    companion object {
        suspend fun PipelineContext<*, ApplicationCall>.getSession(): Session {
            // Get session from call.sessions if it exists, otherwise create it and set it to the sessions object
            return call.sessions.get() ?: SessionRepository.createSession().also { call.sessions.set(it) }
        }

        /**
         * Adds n to the [Session.nbItemsInCart] count and returns the [Session] object
         * @param n the amount to add
         */
        suspend fun Session.addItemsToCartCount(n: Int) : Session {
            return SessionRepository.updateSession(
                this.copy(nbItemsInCart = this.nbItemsInCart + n)
            )
        }

        /**
         * Adds 1 to the [Session.nbItemsInCart] count and returns the [Session] object
         * @param n the amount to add
         */
        suspend fun Session.removeItemToCartCount(n: Int) : Session {
            return addItemsToCartCount(-n)
        }

        /**
         * Sets the [Session.nbItemsInCart] count and returns the [Session] object
         */
        suspend fun Session.setItemsCartCount(amount : Int) : Session {
            return SessionRepository.updateSession(
                this.copy(nbItemsInCart = amount)
            )
        }
    }
}