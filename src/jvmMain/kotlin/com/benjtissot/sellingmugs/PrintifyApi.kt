package com.benjtissot.sellingmugs

import com.benjtissot.sellingmugs.entities.printify.*
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*

/**
 * Commonly owned Printify API to be accessed both by the front-end and the backend
 */
// TODO: turn all the front-end calls to this API into regular server calls that will then propagate
var jsonPrintifyClient = HttpClient {
    install(ContentNegotiation) {
        json()
    }
    defaultRequest {
        url("https://fast-earth-36264.herokuapp.com/https://api.printify.com/v1/")
        header("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiIzN2Q0YmQzMDM1ZmUxMWU5YTgwM2FiN2VlYjNjY2M5NyIsImp0aSI6IjVkZDc2OTg0YmJjMWNiZTA0MDNmZjYyNTZiMjdmNWIyZmNkN2U5MjFmNTU3ZGNiOTQ2NmUxOGRmNGYzZGVjZWQwMGQyYWNkMWRhM2FjNjc2IiwiaWF0IjoxNjg0OTE2MjE4LjY0NDU1NCwibmJmIjoxNjg0OTE2MjE4LjY0NDU1NiwiZXhwIjoxNzE2NTM4NjE4LjYzNjg1OCwic3ViIjoiMTMxNTg1MDAiLCJzY29wZXMiOlsic2hvcHMubWFuYWdlIiwic2hvcHMucmVhZCIsImNhdGFsb2cucmVhZCIsIm9yZGVycy5yZWFkIiwib3JkZXJzLndyaXRlIiwicHJvZHVjdHMucmVhZCIsInByb2R1Y3RzLndyaXRlIiwid2ViaG9va3MucmVhZCIsIndlYmhvb2tzLndyaXRlIiwidXBsb2Fkcy5yZWFkIiwidXBsb2Fkcy53cml0ZSIsInByaW50X3Byb3ZpZGVycy5yZWFkIl19.AYWhOEIO1GzGNbihnmJvq0CBVQ7_iw05T6-q2PMjQ4BvaHiVugTPKrQHmSh7QooY2f_x3RVwzWpd7QLy0hA")
        header("origin", "localhost:9090")  // needed for this to work, TODO change this when production lol
    }
}

const val shopId = 8965065

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
        setBody(Publish())
    }.status
}


/**
 * Confirms the publication of a product on the printify store
 * @param productId the Id of the product that was published
 */
suspend fun apiPublishingSuccessfulProduct(productId: String) : HttpStatusCode {
    return jsonPrintifyClient.post("shops/$shopId/products/$productId/publishing_succeeded.json"){
        contentType(ContentType.Application.Json)
        setBody(PublishSucceed(External(productId, "localhost:9090")))
    }.status
}