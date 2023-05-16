package com.benjtissot.sellingmugs

import com.benjtissot.sellingmugs.entities.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.logging.*
import org.komputing.khash.sha256.extensions.sha256

private val LOG = KtorSimpleLogger("Api.kt")

var jsonClient = HttpClient {
    install(ContentNegotiation) {
        json()
    }
}

fun updateClientWithToken(token: String) {
    jsonClient = HttpClient {
        install(ContentNegotiation) {
            json()
        }
        if (token.isNotBlank()){
            defaultRequest {
                header("Authorization", "Bearer $token")
            }
        }
    }
}

// Check Redirect
suspend fun checkRedirect(): String {
    return jsonClient.post(CHECK_REDIRECT_PATH).body()
}

// Login methods

suspend fun login(email: String, hashedPassword: String): HttpResponse {
    // Only need user email and password for login
    val user = User("", "", "", email, hashedPassword, Const.UserType.CLIENT, "")
    return jsonClient.post(LOGIN_BACKEND_PATH) {
        contentType(ContentType.Application.Json)
        setBody(user)
    }
}

suspend fun register(user: User): HttpResponse {
    return jsonClient.post(REGISTER_PATH) {
        contentType(ContentType.Application.Json)
        setBody(user)
    }
}

suspend fun logout(): HttpResponse {
    // Only need user email and password for login
    return jsonClient.get(LOGOUT_PATH)
}

// Get session
suspend fun getSession(): Session {
    val httpResponse = jsonClient.get(Session.path)
    return httpResponse.body()
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


// Cart
suspend fun addMugToCart(mug: Mug){
    jsonClient.post(CART_PATH + Mug.path) {
        contentType(ContentType.Application.Json)
        setBody(mug)
    }
}

suspend fun getCart() : Cart {
    return jsonClient.get(Cart.path).body()
}


// get User Info

suspend fun getUserInfo() : String {
    val httpResponse = jsonClient.get(USER_INFO_MESSAGE_PATH)
    return httpResponse.body()
}

suspend fun postDummyLogin() : HttpResponse{
    val user = User("","", "", "123", "123".sha256().toString(), Const.UserType.ADMIN, "23")

    LOG.debug("Posting dummy login")
    return login(user.email, user.passwordHash)
}
suspend fun postDummyRegister(): HttpResponse {
    val user = User("123", "Benjamin", "Tissot", "123", "123".sha256().toString(), Const.UserType.ADMIN, "23")

    LOG.debug("Posting dummy register")
    return register(user)
}