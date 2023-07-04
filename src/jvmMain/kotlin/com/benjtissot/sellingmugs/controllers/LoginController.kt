package com.benjtissot.sellingmugs.controllers

import com.benjtissot.sellingmugs.CHECKOUT_PATH
import com.benjtissot.sellingmugs.LOGIN_BACKEND_PATH
import com.benjtissot.sellingmugs.LOGOUT_PATH
import com.benjtissot.sellingmugs.REGISTER_PATH
import com.benjtissot.sellingmugs.entities.Session
import com.benjtissot.sellingmugs.services.BadCredentialsException
import com.benjtissot.sellingmugs.services.LoginService.Companion.login
import com.benjtissot.sellingmugs.services.LoginService.Companion.logout
import com.benjtissot.sellingmugs.services.LoginService.Companion.register
import com.benjtissot.sellingmugs.services.SessionService.Companion.getSession
import com.benjtissot.sellingmugs.services.UserAlreadyExistsException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.util.logging.*
import redirectPath
import java.util.*

private val LOG = KtorSimpleLogger("LoginController.kt")
fun Route.loginRouting(){

    route(LOGIN_BACKEND_PATH) {
        authenticate("auth-jwt") {
            get {
                // Returns a boolean whether or not the user is still logged in
                val principal = call.principal<JWTPrincipal>()
                call.respond((principal?.expiresAt?.after(Date(System.currentTimeMillis())) ?: false).toString())
            }
        }

        // Logging in should respond the JWT token
        post {
            try{
                call.sessions.set(login(call.receive(), getSession()))
                call.respond(call.sessions.get<Session>()?.jwtToken ?: "")
            } catch (badCred : BadCredentialsException){
                call.respond(HttpStatusCode.Conflict)
            } catch (e: Exception){
                e.printStackTrace()
                call.respond(HttpStatusCode.InternalServerError)
            }

        }
        delete() {
            call.respond(HttpStatusCode.OK)
        }
    }

    route(REGISTER_PATH) {

        post {
            try{
                call.sessions.set(register(call.receive(), getSession()))
                call.respond(HttpStatusCode.OK)
            } catch (alreadyExists : UserAlreadyExistsException){
                call.respond(HttpStatusCode.Conflict)
            } catch (e: Exception){
                call.respond(HttpStatusCode.InternalServerError)
            }
        }

        delete() {
            call.respond(HttpStatusCode.OK)
        }
    }

    route(LOGOUT_PATH) {
        get {
            call.sessions.get<Session>()?.let {
                try{
                    call.sessions.set(logout(it))
                    call.respond(HttpStatusCode.OK)
                } catch (e: Exception){
                    e.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }
        }
        post {
            call.respond(HttpStatusCode.OK)
        }
        delete() {
            call.respond(HttpStatusCode.OK)
        }
    }
}