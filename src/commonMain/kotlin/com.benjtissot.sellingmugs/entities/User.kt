package com.benjtissot.sellingmugs.entities

import com.benjtissot.sellingmugs.Const
import com.benjtissot.sellingmugs.USER_OBJECT_PATH
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("_id") val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val passwordHash: String,
    val userType: Const.UserType,
    var addressInfoId: String, // todo: "save cart" instead of personal info
    ){

    companion object {
        // Idea is that we don't need to define a route and requests in strings. Any changes only need to come from the models
        // then the client and the server are adjusted automatically
        const val path = USER_OBJECT_PATH

        /**
         * Returns a dummy user
         * @param seed a seed, returns an admin if [seed]<=1
         */
        fun dummy(uuid: String, seed: Int) : User {
            val seedString = seed.toString()
            return User(uuid, seedString, seedString, seedString, seedString,
                if (seed>=2) Const.UserType.CLIENT else Const.UserType.ADMIN, seedString)
        }
    }

    fun getNameInitial() : String{
        return "${firstName[0]}. $lastName"
    }
}

/**
 * Class used to send user login information from front-end to back-end
 */
@Serializable
data class LoginInfo(
    val email: String,
    val passwordHash: String,
){}



/**
 * Class used to send user registering information from front-end to back-end
 */
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

    fun toLoginInfo() : LoginInfo {
        return LoginInfo(email, passwordHash)
    }

    companion object {

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