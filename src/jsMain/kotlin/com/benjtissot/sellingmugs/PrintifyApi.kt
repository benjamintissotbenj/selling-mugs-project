package com.benjtissot.sellingmugs

import com.benjtissot.sellingmugs.entities.printify.*
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.*
import org.w3c.files.File


var jsonPrintifyClient = HttpClient {
    install(ContentNegotiation) {
        json()
    }
    defaultRequest {
        header("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiIzN2Q0YmQzMDM1ZmUxMWU5YTgwM2FiN2VlYjNjY2M5NyIsImp0aSI6IjVkZDc2OTg0YmJjMWNiZTA0MDNmZjYyNTZiMjdmNWIyZmNkN2U5MjFmNTU3ZGNiOTQ2NmUxOGRmNGYzZGVjZWQwMGQyYWNkMWRhM2FjNjc2IiwiaWF0IjoxNjg0OTE2MjE4LjY0NDU1NCwibmJmIjoxNjg0OTE2MjE4LjY0NDU1NiwiZXhwIjoxNzE2NTM4NjE4LjYzNjg1OCwic3ViIjoiMTMxNTg1MDAiLCJzY29wZXMiOlsic2hvcHMubWFuYWdlIiwic2hvcHMucmVhZCIsImNhdGFsb2cucmVhZCIsIm9yZGVycy5yZWFkIiwib3JkZXJzLndyaXRlIiwicHJvZHVjdHMucmVhZCIsInByb2R1Y3RzLndyaXRlIiwid2ViaG9va3MucmVhZCIsIndlYmhvb2tzLndyaXRlIiwidXBsb2Fkcy5yZWFkIiwidXBsb2Fkcy53cml0ZSIsInByaW50X3Byb3ZpZGVycy5yZWFkIl19.AYWhOEIO1GzGNbihnmJvq0CBVQ7_iw05T6-q2PMjQ4BvaHiVugTPKrQHmSh7QooY2f_x3RVwzWpd7QLy0hA")
    }
}

suspend fun postProduct(mugProduct: MugProduct): HttpResponse {
    return jsonPrintifyClient.post("https://fast-earth-36264.herokuapp.com/https://api.printify.com/v1/shops/8965065/products.json"){
        contentType(ContentType.Application.Json)
        setBody(mugProduct)

    }
}

suspend fun publishProduct(productId: String) {
    jsonPrintifyClient.post("https://fast-earth-36264.herokuapp.com/https://api.printify.com/v1/shops/8965065/products/$productId/publish.json"){
        contentType(ContentType.Application.Json)
        setBody(Publish())
    }

    jsonPrintifyClient.post("https://fast-earth-36264.herokuapp.com/https://api.printify.com/v1/shops/8965065/products/$productId/publishing_succeeded.json"){
        contentType(ContentType.Application.Json)
        setBody(PublishSucceed(External(productId, "localhost:9090")))
    }
}

/**
 * @param imageFile is a 64base encoded string of the image to upload
 */
suspend fun uploadImage(imageFile: ImageForUpload) : HttpResponse {
    return jsonPrintifyClient.post("https://fast-earth-36264.herokuapp.com/https://api.printify.com/v1/uploads/images.json"){
        contentType(ContentType.Application.Json)
        setBody(imageFile)
    }
}