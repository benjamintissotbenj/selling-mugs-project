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

suspend fun getMugList(): List<MugListItem> {
    return jsonClient.get(MugListItem.path).body()
}

suspend fun addMugListItem(mugListItem: MugListItem) {
    jsonClient.post(MugListItem.path) {
        contentType(ContentType.Application.Json)
        setBody(mugListItem)
    }
}

suspend fun deleteMugListItem(mugListItem: MugListItem) {
    jsonClient.delete(MugListItem.path + "/${mugListItem.id}")
}