package com.benjtissot.sellingmugs.controllers

import com.benjtissot.sellingmugs.USER_INFO_MESSAGE_PATH
import com.benjtissot.sellingmugs.USER_OBJECT_PATH
import com.benjtissot.sellingmugs.repositories.UserRepository.Companion.deleteUser
import com.benjtissot.sellingmugs.repositories.UserRepository.Companion.getUserList
import com.benjtissot.sellingmugs.repositories.UserRepository.Companion.updateUser
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.logging.Logger

fun Route.userRouting(){

    // val LOG = Logger.getLogger(this.javaClass.name)

    route(USER_OBJECT_PATH) {

        authenticate("auth-jwt") {
            get {
                call.respond(getUserList())
            }
            post {
                updateUser(call.receive())
                call.respond(HttpStatusCode.OK)
            }
            delete() {
                deleteUser(call.receive())
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}