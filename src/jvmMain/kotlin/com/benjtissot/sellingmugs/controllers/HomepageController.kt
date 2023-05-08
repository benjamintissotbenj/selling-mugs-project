package com.benjtissot.sellingmugs.controllers

import com.benjtissot.sellingmugs.HOMEPAGE_PATH
import com.benjtissot.sellingmugs.entities.Click
import com.benjtissot.sellingmugs.entities.Session
import com.benjtissot.sellingmugs.genUuid
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

fun Route.homepageRouting(){

    route(HOMEPAGE_PATH) {
        get {
            call.respondText(
                this::class.java.classLoader.getResource("index.html")!!.readText(),
                ContentType.Text.Html
            )
        }
        post {
            call.respond(HttpStatusCode.OK)
        }
        delete() {
            call.respond(HttpStatusCode.OK)
        }
    }
    static(HOMEPAGE_PATH) {
        resources("")
    }
}