package com.benjtissot.sellingmugs.controllers

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.entities.Session
import com.benjtissot.sellingmugs.entities.User
import com.benjtissot.sellingmugs.repositories.ClickDataRepository
import com.benjtissot.sellingmugs.repositories.SessionRepository
import com.benjtissot.sellingmugs.repositories.sessionCollection
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import java.util.logging.Logger

fun Route.clickRouting(){

    val LOG = Logger.getLogger(this.javaClass.name)

    // updating a ClickData because we add a click of type click_type
    route ("$CLICK_PATH/{click_data_id}/{click_type}") {
        post {
            val clickDataId: String = call.parameters["click_data_id"] ?: error("Invalid post request")
            val clickTypeStr: String = call.parameters["click_type"] ?: error("Invalid post request")
            LOG.info("Click type is $clickTypeStr")
            val clickType: Const.ClickType = Const.ClickType.valueOf(clickTypeStr)
            try {
                ClickDataRepository.addClickById(clickDataId, clickType)
                call.respond(HttpStatusCode.OK)
            } catch (e: Exception){
                call.respond(HttpStatusCode.BadRequest)
            }
        }
    }
}