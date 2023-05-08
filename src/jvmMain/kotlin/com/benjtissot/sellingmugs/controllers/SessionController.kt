package com.benjtissot.sellingmugs.controllers

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.entities.Click
import com.benjtissot.sellingmugs.entities.Session
import com.benjtissot.sellingmugs.entities.User
import database
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import java.util.logging.Logger

val sessionCollection = database.getCollection<Session>()
fun Route.sessionRouting(){

    val LOG = Logger.getLogger(this.javaClass.name)
    // TODO: deal with this at some point

    route(SESSION_PATH) {
        get {
            val userSession = call.sessions.get<Session>()
            userSession?.let { call.respond(userSession)} ?: call.respond(HttpStatusCode.BadRequest)
        }
        post {
            call.respond(HttpStatusCode.OK)
        }
        delete() {
            call.respond(HttpStatusCode.OK)
        }
        route (USER_PATH) {
            post {
                val userSession = call.sessions.get<Session>()?.copy()
                LOG.info("old UserSession is $userSession")
                // If session is found, set session user to received user
                userSession?.let{
                    val newSession = userSession.copy(user = call.receive<User>())
                    LOG.info("New UserSession is $userSession")
                    try {
                        // TODO: replace insert by UPDATE OR INSERT kind of stuff
                        sessionCollection.insertOne(newSession)
                        call.sessions.set(newSession)
                        call.respond(HttpStatusCode.OK)
                    } catch (e: Exception){
                        call.respond(HttpStatusCode.BadGateway)
                    }
                }
            }
        }

        // updating a session because we add a click of type click_type
        route ("$CLICK_PATH/{click_type}") {
            post {
                val clickTypeStr: String = call.parameters["click_type"] ?: error("Invalid post request")
                LOG.info("Click type is $clickTypeStr")
                val clickType: Const.ClickType = Const.ClickType.valueOf(clickTypeStr)
                val userSession = call.sessions.get<Session>()?.copy()
                LOG.info("clicktype: UserSession is $userSession")
                // If session is found, set session user to received user
                userSession?.let{


                    LOG.info("clicktype: TEST TO SEE IF ADDING A CLICK IS THE PROBLEM")
                    //userSession.clicks.add(Click(genUuid().toString(), clickType))

                    try {
                        // TODO: Optimise this with a "push" operation
                        //sessionCollection.updateOne(filter = Session::id eq userSession.id, push(SOMETHING))
                        LOG.info("clicktype: updating UserSession $userSession")
                        sessionCollection.updateOneById(userSession.id, userSession)
                        call.sessions.set(userSession)
                        call.respond(HttpStatusCode.OK)
                    } catch (e: Exception){
                        call.respond(HttpStatusCode.BadGateway)
                    }
                }
            }
        }
    }
}