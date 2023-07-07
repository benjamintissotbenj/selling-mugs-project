package com.benjtissot.sellingmugs

import com.benjtissot.sellingmugs.entities.printify.*
import com.benjtissot.sellingmugs.entities.printify.order.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

/**
 * Back-end owned Printify API
 */
var jsonPrintifyClient : HttpClient = HttpClient {
    install(ContentNegotiation) {
        json(Json{ ignoreUnknownKeys = true })
    }
    install(Logging){
        level = LogLevel.BODY
        filter { request ->
            !request.url.pathSegments.contains("uploads")
        }
    }
    defaultRequest {
        url("https://fast-earth-36264.herokuapp.com/https://api.printify.com/v1/")
        header("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiIzN2Q0YmQzMDM1ZmUxMWU5YTgwM2FiN2VlYjNjY2M5NyIsImp0aSI6IjVkZDc2OTg0YmJjMWNiZTA0MDNmZjYyNTZiMjdmNWIyZmNkN2U5MjFmNTU3ZGNiOTQ2NmUxOGRmNGYzZGVjZWQwMGQyYWNkMWRhM2FjNjc2IiwiaWF0IjoxNjg0OTE2MjE4LjY0NDU1NCwibmJmIjoxNjg0OTE2MjE4LjY0NDU1NiwiZXhwIjoxNzE2NTM4NjE4LjYzNjg1OCwic3ViIjoiMTMxNTg1MDAiLCJzY29wZXMiOlsic2hvcHMubWFuYWdlIiwic2hvcHMucmVhZCIsImNhdGFsb2cucmVhZCIsIm9yZGVycy5yZWFkIiwib3JkZXJzLndyaXRlIiwicHJvZHVjdHMucmVhZCIsInByb2R1Y3RzLndyaXRlIiwid2ViaG9va3MucmVhZCIsIndlYmhvb2tzLndyaXRlIiwidXBsb2Fkcy5yZWFkIiwidXBsb2Fkcy53cml0ZSIsInByaW50X3Byb3ZpZGVycy5yZWFkIl19.AYWhOEIO1GzGNbihnmJvq0CBVQ7_iw05T6-q2PMjQ4BvaHiVugTPKrQHmSh7QooY2f_x3RVwzWpd7QLy0hA")
        header("origin", "${ConfigConst.HOST}:${ConfigConst.PORT}")  // needed for this to work,
    }
}


var shopId = System.getenv(Const.PRINTIFY_STORE_ID_STRING)?.toInt() ?: 8965065
// TODO change " " variables into const variables in Const folder
/**
 *
 * IMAGE
 *
 */

/**
 * Uploads an image to the printify account
 * @param imageFile is a 64base encoded string of the image to upload
 */
suspend fun apiUploadImage(imageFile: ImageForUpload) : HttpResponse {
    val httpResponse = jsonPrintifyClient.post("uploads/images.json"){
        contentType(ContentType.Application.Json)
        setBody(imageFile)
    }
    return httpResponse
}


/**
 *
 * PRODUCTS
 *
 */

/**
 * Deletes an image to the printify account
 * @param productId the product to be deleted
 */
suspend fun apiDeleteProduct(productId: String) : HttpResponse {
    return jsonPrintifyClient.delete("shops/$shopId/products/$productId.json")
}

/**
 * Creates a product on the printify store
 * @param mugProduct is the product to be created on the store
 */
suspend fun apiCreateProduct(mugProduct: MugProduct): HttpResponse {
    return jsonPrintifyClient.post("shops/$shopId/products.json"){
        contentType(ContentType.Application.Json)
        setBody(mugProduct)

    }
}

/**
 * Publishes a created product on the printify store
 * @param productId the Id of the product to be published
 */
suspend fun apiPublishProduct(productId: String) : HttpStatusCode {
    return jsonPrintifyClient.post("shops/$shopId/products/$productId/publish.json"){
        contentType(ContentType.Application.Json)
        setBody(Publish.default())
    }.status
}


/**
 * Confirms the publication of a product on the printify store
 * @param productId the Id of the product that was published
 */
suspend fun apiPublishingSuccessfulProduct(productId: String) : HttpStatusCode {
    return jsonPrintifyClient.post("shops/$shopId/products/$productId/publishing_succeeded.json"){
        contentType(ContentType.Application.Json)
        setBody(PublishSucceed(External(productId, "${ConfigConst.HOST}:${ConfigConst.PORT}")))
    }.status
}

/**
 * Gets a product from the store
 * @param productId the printify id of the product to get
 * @return a [HttpResponse] object that holds all the information concerning the product
 */
suspend fun apiGetProduct(productId: String) : HttpResponse {
    return jsonPrintifyClient.get("shops/$shopId/products/$productId.json")
}

/**
 * Updates a product from the store
 * @param productId the printify id of the product to get
 * @param updatedProductImage the product to be updated
 * @return a [HttpResponse] object that holds all the information concerning the product
 */
suspend fun apiUpdateProductImage(productId: String, updatedProductImage: UpdateProductImage) : HttpResponse {
    return jsonPrintifyClient.put("shops/$shopId/products/$productId.json"){
        contentType(ContentType.Application.Json)
        setBody(updatedProductImage)
    }
}

/**
 * Updates a product from the store
 * @param productId the printify id of the product to get
 * @param updatedProductTitleDesc the product to be updated
 * @return a [HttpResponse] object that holds all the information concerning the product
 */
suspend fun apiUpdateProductTitleDesc(productId: String, updatedProductTitleDesc: UpdateProductTitleDesc) : HttpResponse {
    return jsonPrintifyClient.put("shops/$shopId/products/$productId.json"){
        contentType(ContentType.Application.Json)
        setBody(updatedProductTitleDesc)
    }
}


/**
 *
 * ORDERS
 *
 */


/**
 * Gets a specific order from Printify
 * @param orderPrintifyId the printify id of the order to get
 * @return a [HttpResponse] object that holds all the information concerning the order if it is found
 */
suspend fun apiGetOrder(orderPrintifyId: String) : HttpResponse {
    return jsonPrintifyClient.get("shops/$shopId/orders/$orderPrintifyId.json")
}


/**
 * Places an order to Printify
 * @param order the order to place to Printify
 * @return a [PrintifyOrderPushResult] object that holds all the information concerning the status of the
 * push order if it fails, and the printify ID if it is a success
 */
suspend fun apiPlaceOrder(order: Order) : PrintifyOrderPushResult {
    val httpResponse = jsonPrintifyClient.post("shops/$shopId/orders.json"){
        contentType(ContentType.Application.Json)
        setBody(order)
    }
    val result = if (httpResponse.status == HttpStatusCode.BadRequest){
        httpResponse.body<PrintifyOrderPushFail>()
    } else {
        httpResponse.body<PrintifyOrderPushSuccess>()
    }
    return result
}


/**
 * Cancels a specific order from Printify
 * @param orderPrintifyId the printify id of the order to be cancelled
 * @return a [HttpStatusCode] that tells us if the order was cancelled
 */
suspend fun apiCancelOrder(orderPrintifyId: String) : HttpStatusCode {
    return jsonPrintifyClient.post("shops/$shopId/orders/$orderPrintifyId/cancel.json").status
}


/**
 * Calculates the shipping costs for an order from Printify
 * @param orderToCalculateShippingCosts the formatted order to calculate shipping costs
 * @return a [ShippingCosts] object that holds the different shipping costs (standard/express)
 */
suspend fun apiCalculateOrderShippingCost(orderToCalculateShippingCosts: OrderToCalculateShippingCosts) : HttpResponse {
    return jsonPrintifyClient.post("shops/$shopId/orders/shipping.json"){
        contentType(ContentType.Application.Json)
        setBody(orderToCalculateShippingCosts)
    }
}


/**
 * Sends an order to production in Printify
 * @param orderPrintifyId the printify id of the order to be sent to production
 * @return a [HttpStatusCode] that tells us if the order was sent to production
 */
suspend fun apiSendOrderToProduction(orderPrintifyId: String) : HttpResponse {
    return jsonPrintifyClient.post("shops/$shopId/orders/$orderPrintifyId/send_to_production.json")
}