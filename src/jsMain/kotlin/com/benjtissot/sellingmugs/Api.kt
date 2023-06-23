package com.benjtissot.sellingmugs

import com.benjtissot.sellingmugs.entities.*
import com.benjtissot.sellingmugs.entities.printify.*
import com.benjtissot.sellingmugs.entities.printify.order.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.logging.*
import kotlinx.serialization.json.Json

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

suspend fun isLoggedIn(): HttpResponse {
    return jsonClient.get(LOGIN_BACKEND_PATH)
}

suspend fun login(loginInfo: LoginInfo): HttpResponse {
    return jsonClient.post(LOGIN_BACKEND_PATH) {
        contentType(ContentType.Application.Json)
        setBody(loginInfo)
    }
}

suspend fun register(registerInfo: RegisterInfo): HttpResponse {
    return jsonClient.post(REGISTER_PATH) {
        contentType(ContentType.Application.Json)
        setBody(registerInfo)
    }
}

suspend fun logout(): HttpResponse {
    // Reset the client to delete JWT and updating the session
    updateClientWithToken("")
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

suspend fun getMugByPrintifyId(printifyId: String): Mug? {
    val httpResponse = jsonClient.get("${Mug.path}/$printifyId")
    return if (httpResponse.status == HttpStatusCode.OK) httpResponse.body() else null
}


// Cart
suspend fun addMugToCart(mug: Mug?){
    mug?.let {
        jsonClient.post(CART_PATH + Mug.path) {
            contentType(ContentType.Application.Json)
            setBody(mug)
        }
    }
}

suspend fun getCart() : Cart {
    return jsonClient.get(Cart.path).body()
}

suspend fun removeMugCartItemFromCart(mugCartItem: MugCartItem){
    jsonClient.delete(CART_PATH + MugCartItem.path) {
        contentType(ContentType.Application.Json)
        setBody(mugCartItem)
    }
}


// get User Info

suspend fun getUserInfo() : String {
    val httpResponse = jsonClient.get(USER_INFO_MESSAGE_PATH)
    return httpResponse.body()
}

suspend fun updateUser(user: User) : HttpResponse {
    return jsonClient.post(USER_OBJECT_PATH){
        contentType(ContentType.Application.Json)
        setBody(user)
    }
}

suspend fun getUserList() : List<User> {
    return jsonClient.get(USER_OBJECT_PATH).body()
}

// Printify methods, relayed by the backend

/**
 * @param imageFile is a 64base encoded string of the image to upload
 * @return the [Artwork.imageURL] for the uploaded image
 */
suspend fun uploadImage(imageFile: ImageForUpload, public : Boolean = true) : ImageForUploadReceive? {
    return jsonClient.post("$PRINTIFY_PATH$UPLOAD_IMAGE_PATH/$public"){
        contentType(ContentType.Application.Json)
        setBody(imageFile)
    }.body()
}

/**
 * @return the product id
 */
suspend fun createProduct(mugProductInfo: MugProductInfo): HttpResponse {
    return jsonClient.post(PRINTIFY_PATH + CREATE_PRODUCT_PATH){
        contentType(ContentType.Application.Json)
        setBody(mugProductInfo)

    }
}

/**
 * Publishes the product
 * @param productId the product ID
 */
suspend fun publishProduct(productId: String) {
    jsonClient.post(PRINTIFY_PATH + PUBLISH_PRODUCT_PATH){
        contentType(ContentType.Application.Json)
        setBody(productId)
    }
}

/**
 * Gets a product from printify
 * @param productId the product ID
 */
suspend fun getProduct(productId: String) : ReceiveProduct {
    return jsonClient.get("$PRINTIFY_PATH$PRODUCT_PATH/$productId").body()
}

/**
 * Updates a product from the store
 * @param productId the printify id of the product to get
 * @param updatedProduct the product to be updated
 * @return a [ReceiveProduct] object that holds all the information concerning the product
 */
suspend fun putProduct(productId: String, updatedProductImage: UpdateProductImage) : ReceiveProduct {
    return jsonClient.put("$PRINTIFY_PATH$PRODUCT_PATH/$productId") {
        contentType(ContentType.Application.Json)
        setBody(updatedProductImage)
    }.body()
}

/**
 * Gets the list of preview images sources for a product from printify
 * @param productId the product ID
 */
suspend fun getProductPreviewImages(productId: String) : List<String> {
    return jsonClient.get("$PRINTIFY_PATH$PRODUCT_PATH/$productId$IMAGES_PATH").body()
}

/**
 * Creates an order in the back-end containing the different products selected in the cart
 */
suspend fun createOrder(addressTo: AddressTo) : HttpResponse {
    return jsonClient.post("${Order.path}$CREATE_ORDER_PATH"){
        contentType(ContentType.Application.Json)
        setBody(addressTo)
    }
}

/**
 * Gets a pushResult by cart id
 * @param cartId the [Cart.id] for which we want to retrieve the push result
 */
suspend fun getOrderPushResultByCartId(cartId : String) : PrintifyOrderPushResult? {
    val httpResponse = jsonClient.get("${Order.path}$PUSH_RESULT_PATH?cartId=$cartId")
    return if (httpResponse.status == HttpStatusCode.BadRequest){
        null
    } else {
        val pushResultString = httpResponse.body<String>()
        LOG.debug("Push result string is $pushResultString")
        val decoded = Json.decodeFromString(PushResultSerializer, pushResultString)
        decoded
    }
}

/**
 * Retrieves a user's list of orders
 * @param userId the id of the [User] for which to retrieve the list of past [Order]s
 * @return a [List] of [Order]s if the list exists, an empty list otherwise (shouldn't happen, but we never know)
 */
suspend fun getUserOrderList(userId: String) : List<Order> {

    val httpResponse = jsonClient.get("${Order.path}/$userId")
    return if (httpResponse.status == HttpStatusCode.BadRequest){
        emptyList()
    } else {
        httpResponse.body()
    }
}



/**
 * Retrieves a list of [MugCartItem]s depending on an [Order.line_items] for display
 * @param orderId the local id of the [Order]
 * @return a [List] of [MugCartItem] corresponding to the order's [Order.line_items]
 */
suspend fun getOrderLineItemsAsMugCartItems(orderId: String) : List<MugCartItem> {
    val httpResponse = jsonClient.get("${Order.path}${MugCartItem.path}?orderId=$orderId")
    return if (httpResponse.status == HttpStatusCode.BadRequest){
        emptyList()
    } else {
        httpResponse.body()
    }
}