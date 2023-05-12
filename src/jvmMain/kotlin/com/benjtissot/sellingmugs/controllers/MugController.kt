package com.benjtissot.sellingmugs.controllers

import com.benjtissot.sellingmugs.entities.Artwork
import com.benjtissot.sellingmugs.entities.Mug
import com.benjtissot.sellingmugs.services.ArtworkService.Companion.deleteArtwork
import com.benjtissot.sellingmugs.services.ArtworkService.Companion.getArtworkList
import com.benjtissot.sellingmugs.services.ArtworkService.Companion.insertNewArtwork
import com.benjtissot.sellingmugs.services.MugService.Companion.deleteMug
import com.benjtissot.sellingmugs.services.MugService.Companion.getMugList
import com.benjtissot.sellingmugs.services.MugService.Companion.insertNewMug
import database
import io.ktor.server.application.*
import io.ktor.server.routing.*
import java.util.logging.Logger


val mugCollection = database.getCollection<Mug>()
val artworkCollection = database.getCollection<Artwork>()
fun Route.mugRouting(){

    val LOG = Logger.getLogger(this.javaClass.name)

    route(Mug.path) {
        get {
            getMugList()
        }
        post {
            insertNewMug()
        }
        delete("/{id}") {
            val id = call.parameters["id"] ?: error("Invalid delete request")
            deleteMug(id)
        }
    }

    route(Artwork.path) {
        get {
            getArtworkList()
        }
        post {
            LOG.severe("Posting Artwork")
            insertNewArtwork()
        }
        delete("/{id}") {
            val id = call.parameters["id"] ?: error("Invalid delete request")
            deleteArtwork(id)
        }
    }
}