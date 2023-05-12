package com.benjtissot.sellingmugs.services

import com.benjtissot.sellingmugs.controllers.LOG
import com.benjtissot.sellingmugs.controllers.mugCollection
import com.benjtissot.sellingmugs.entities.Mug
import com.benjtissot.sellingmugs.genUuid
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import org.litote.kmongo.eq

class MugService {
    companion object {

        suspend fun PipelineContext<*, ApplicationCall>.getMugList(){
            call.respond(mugCollection.find().toList())
        }

        suspend fun PipelineContext<*, ApplicationCall>.insertNewMug(){
            LOG.severe("Posting Mug")
            mugCollection.insertOne(call.receive<Mug>().copy(genUuid().toString()))
            call.respond(HttpStatusCode.OK)
        }

        suspend fun PipelineContext<*, ApplicationCall>.deleteMug(id: String){
            mugCollection.deleteOne(Mug::id eq id) //type safe
            call.respond(HttpStatusCode.OK)
        }
    }
}