package com.benjtissot.sellingmugs

import com.benjtissot.sellingmugs.Pages.LOG
import com.benjtissot.sellingmugs.entities.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
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

suspend fun recordClick(clickDataId: String, clickType: String) {

    jsonClient.post(Click.path + "/$clickDataId/$clickType") {
        contentType(ContentType.Application.Json)
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


// get User Info

suspend fun getUserInfo() : String {
    val httpResponse = jsonClient.get(USER_INFO_PATH)
    return httpResponse.body()
}

// get User Info

suspend fun postDummyLogin() {
    val user = User("123","Benjamin", "Tissot", "123", "123", Const.UserType.ADMIN, "23")
    jsonClient.post(LOGIN_PATH) {
        contentType(ContentType.Application.Json)
        setBody(user)
    }
}
suspend fun postDummyRegister(): HttpResponse {
    val user = User("123","Benjamin", "Tissot", "123", "123", Const.UserType.ADMIN, "23")

    LOG.debug("Posting dummy register")
    val httpResponse = jsonClient.post(REGISTER_PATH) {
        contentType(ContentType.Application.Json)
        setBody(user)
    }

    return httpResponse
}