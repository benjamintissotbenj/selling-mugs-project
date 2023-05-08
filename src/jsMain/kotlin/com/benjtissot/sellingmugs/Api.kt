package com.benjtissot.sellingmugs

import com.benjtissot.sellingmugs.entities.Artwork
import com.benjtissot.sellingmugs.entities.Mug
import com.benjtissot.sellingmugs.entities.Session
import com.benjtissot.sellingmugs.entities.User
import io.ktor.http.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*

val jsonClient = HttpClient {
    install(ContentNegotiation) {
        json()
    }
}

// Get session
suspend fun getSession(): Session {
    return jsonClient.get(Session.path).body()
}

suspend fun setUser(user: User) {
    jsonClient.post(Session.path+User.path) {
        contentType(ContentType.Application.Json)
        setBody(user)
    }
}


// MugList
suspend fun getMugList(): List<Mug> {
    return jsonClient.get(Mug.path).body()
}

suspend fun addMugListItem(mugListItem: Mug) {
    jsonClient.post(Mug.path) {
        contentType(ContentType.Application.Json)
        setBody(mugListItem)
    }
}

suspend fun addArtwork(artwork: Artwork) {
    jsonClient.post(Artwork.path) {
        contentType(ContentType.Application.Json)
        setBody(artwork)
    }
}

suspend fun deleteMugListItem(mugListItem: Mug) {
    jsonClient.delete(Mug.path + "/${mugListItem.id}")
}