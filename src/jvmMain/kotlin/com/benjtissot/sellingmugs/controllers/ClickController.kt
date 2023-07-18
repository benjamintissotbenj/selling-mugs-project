package com.benjtissot.sellingmugs.controllers

import com.benjtissot.sellingmugs.Const
import com.benjtissot.sellingmugs.entities.local.Click
import com.benjtissot.sellingmugs.repositories.ClickDataRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.clickRouting(){

    //val LOG = Logger.getLogger(this.javaClass.name)

    // updating a ClickData because we add a click of type click_type
    route ("${Click.path}/{${Const.clickDataId}}/{${Const.clickType}}") {
        post {
            try {
                val clickDataId: String = call.parameters[Const.clickDataId] ?: error("Invalid post request")
                val clickTypeStr: String = call.parameters[Const.clickType] ?: error("Invalid post request")
                val clickType = Const.ClickType.valueOf(clickTypeStr)
                ClickDataRepository.addClickById(clickDataId, clickType)
                call.respond(HttpStatusCode.OK)
            } catch (e: Exception){
                e.printStackTrace()
                call.respond(HttpStatusCode.BadRequest)
            }
        }
    }
}