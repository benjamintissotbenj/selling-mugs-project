import kotlinx.serialization.Serializable

@Serializable
data class MugListItem(val desc: String, val priority: Int) {
    val id: Int = desc.hashCode()

    companion object {
        // Idea is that we don't need to define a route and requests in strings. Any changes only need to come from the models
        // then the client and the server are adjusted automatically
        const val path = "/mugList"
    }
}