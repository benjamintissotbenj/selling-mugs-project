package com.benjtissot.sellingmugs.entities

import com.benjtissot.sellingmugs.Const
import com.benjtissot.sellingmugs.REGISTER_INFO_OBJECT_PATH
import com.benjtissot.sellingmugs.USER_OBJECT_PATH
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterInfo(
    val firstName: String,
    val lastName: String,
    val email: String,
    val passwordHash: String,
    ){

    fun toUser(id: String) : User {
        return User(id, firstName, lastName, email, passwordHash, Const.UserType.CLIENT, "")
    }

    companion object {
        // Idea is that we don't need to define a route and requests in strings. Any changes only need to come from the models
        // then the client and the server are adjusted automatically
        const val path = REGISTER_INFO_OBJECT_PATH

        /**
         * Returns a dummy user
         * @param seed a seed, returns an admin if [seed]<=1
         */
        fun dummy(seed: Int) : RegisterInfo {
            val seedString = seed.toString()
            return RegisterInfo(seedString, seedString, seedString, seedString)
        }
    }
}