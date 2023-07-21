package com.benjtissot.sellingmugs.controllers

import com.benjtissot.sellingmugs.Const
import com.benjtissot.sellingmugs.USER_CUSTOM_MUG_LIST_PATH
import com.benjtissot.sellingmugs.entities.local.Artwork
import com.benjtissot.sellingmugs.entities.local.Category
import com.benjtissot.sellingmugs.entities.local.Mug
import com.benjtissot.sellingmugs.entities.local.MugFilter
import com.benjtissot.sellingmugs.genUuid
import com.benjtissot.sellingmugs.services.ArtworkService.Companion.deleteArtwork
import com.benjtissot.sellingmugs.services.ArtworkService.Companion.getArtworkList
import com.benjtissot.sellingmugs.services.ArtworkService.Companion.insertNewArtwork
import com.benjtissot.sellingmugs.services.CategoryService
import com.benjtissot.sellingmugs.services.MugService
import com.benjtissot.sellingmugs.services.MugService.Companion.deleteMug
import com.benjtissot.sellingmugs.services.MugService.Companion.getMugCount
import com.benjtissot.sellingmugs.services.MugService.Companion.getPublicMugList
import com.benjtissot.sellingmugs.services.MugService.Companion.insertNewMug
import database
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.logging.Logger

val mugCollection = database.getCollection<Mug>()
val artworkCollection = database.getCollection<Artwork>()

fun Route.mugRouting(){

    val LOG = Logger.getLogger(this.javaClass.name)

    route(Mug.path) {
        get {
            val pageNumber: Int? = call.request.queryParameters[Const.pageNumber]?.toIntOrNull()
            val categories = call.parameters.getAll(Const.categories)?.mapNotNull { id -> CategoryService.getCategoryById(id) } ?: emptyList()
            val count : Boolean = call.request.queryParameters[Const.count]?.toBooleanStrictOrNull() ?: false
            if (count){
                call.respond(getMugCount(MugFilter(categories = categories)))
            } else {
                call.respond(getPublicMugList(MugFilter(currentPage = pageNumber, categories = categories)))
            }
        }
        post {
            insertNewMug(call.receive<Mug>().copy(genUuid()))
            call.respond(HttpStatusCode.OK)
        }
        delete("/{${Const.id}}") {
            val id = call.parameters[Const.id] ?: error("Invalid delete request")
            deleteMug(id)
            call.respond(HttpStatusCode.OK)
        }
        route("/{${Const.printifyId}}") {
            get {
                val printifyId = call.parameters[Const.printifyId] ?: error("Invalid delete request")
                MugService.getMugByPrintifyId(printifyId)?.let {
                    call.respond(it)
                } ?: let {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
        }

        route(USER_CUSTOM_MUG_LIST_PATH){
            get {
                val userId = call.request.queryParameters[Const.userId]
                if (!userId.isNullOrBlank()){
                    call.respond(MugService.getUserCustomMugList(userId))
                } else {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }

            route("/{${Const.userId}}/{${Const.mugId}}") {
                post {
                    val userId = call.parameters[Const.userId]
                    val mugId = call.parameters[Const.mugId]
                    if (mugId == null || userId == null){
                        call.respond(HttpStatusCode.BadRequest)
                    } else {
                        MugService.addMugToUserCustomMugList(userId, mugId)
                        call.respond(HttpStatusCode.OK)
                    }
                }
            }
        }


    }

    route(Artwork.path) {
        get {
            call.respond(getArtworkList())
        }
        post {
            LOG.severe("Posting Artwork")
            insertNewArtwork(call.receive<Artwork>().copy(genUuid()))
            call.respond(HttpStatusCode.OK)
        }
        delete("/{id}") {
            val id = call.parameters[Const.id] ?: error("Invalid delete request")
            deleteArtwork(id)
            call.respond(HttpStatusCode.OK)
        }
    }

    route(Category.path){
        get {
            // TODO debug this shit
            val categoryIds = call.parameters.getAll(Const.id)
            if (categoryIds.isNullOrEmpty()) {
                call.respond(CategoryService.getAllCategories())
            } else {
                call.respond(CategoryService.getCategories(categoryIds))
            }
        }

    }
}