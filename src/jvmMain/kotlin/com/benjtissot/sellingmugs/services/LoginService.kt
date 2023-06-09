package com.benjtissot.sellingmugs.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.benjtissot.sellingmugs.ConfigConst
import com.benjtissot.sellingmugs.entities.LoginInfo
import com.benjtissot.sellingmugs.entities.RegisterInfo
import com.benjtissot.sellingmugs.entities.Session
import com.benjtissot.sellingmugs.entities.User
import com.benjtissot.sellingmugs.genUuid
import com.benjtissot.sellingmugs.repositories.SessionRepository
import com.benjtissot.sellingmugs.repositories.UserRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import io.ktor.util.logging.*
import io.ktor.util.pipeline.*
import java.util.*

private val LOG = KtorSimpleLogger("LoginService.kt")
class LoginService {
    companion object {
        val secret = ConfigConst.SECRET
        val issuer = ConfigConst.ISSUER
        val audience = ConfigConst.AUDIENCE
        val myRealm = ConfigConst.REALM

        /**
         * @param user the user to be authenticated
         * @param session the user session to be updated
         * @return the updated session to be set in the call.sessions object
         */
        @Throws(BadCredentialsException::class)
        suspend fun login(loginInfo: LoginInfo, session: Session) : Session {
            //TODO: create userInfo class for transfers
            val authenticatedUser = UserRepository.authenticate(loginInfo)
            LOG.info("User is $loginInfo is authenticated : $authenticatedUser")
            if (authenticatedUser != null){
                val token = JWT.create()
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .withClaim("email", loginInfo.email)
                    .withExpiresAt(Date(System.currentTimeMillis() + 1200000)) //20min, expires after the cookies i.e. session detection
                    .sign(Algorithm.HMAC256(secret))

                // Setting the logged in user to authenticatedUser and jwt to token
                return session.copy(user = authenticatedUser, lastUser= authenticatedUser, jwtToken = token)
                    .also{SessionRepository.updateSession(it)}

            } else {
                // If user is not authenticated, simply throw an exception
                throw BadCredentialsException()
            }
        }


        /**
         * @param registerInfo the user to be registered
         * @param session the user session to be updated
         * @return the updated session to be set in the call.sessions object
         */
        @Throws(UserAlreadyExistsException::class)
        suspend fun register(registerInfo: RegisterInfo, session: Session) : Session {
            if (UserRepository.getUserByEmail(registerInfo.email) != null) {
                // If user is found, error and cannot register new user
                LOG.error("User with email ${registerInfo.email} already exists, sending Conflict")
                throw UserAlreadyExistsException()
            } else {
                LOG.info("User was not found, creating user and logging them in")
                // If user is not found, insert with new UUID
                registerInfo.toUser(id = genUuid()).also {
                    UserRepository.insertUser(it)
                    return login(registerInfo.toLoginInfo(), session)
                }
            }
        }

        /**
         * Method that logs out a user from the currentSession and returns the [Session] object
         * from which the user has been logged out
         */
        suspend fun logout(currentSession: Session) : Session {
        // Only logout from a session that exists
            return SessionRepository.updateSession(currentSession.copy(user = null, jwtToken = ""))
        }

    }
}

class BadCredentialsException : Exception() {

}

class UserAlreadyExistsException : Exception() {

}