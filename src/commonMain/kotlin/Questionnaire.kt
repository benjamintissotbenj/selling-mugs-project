import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Questionnaire(
    @SerialName("_id") val id: String,
    val answers: String,
    //TODO: fill this with the different answers to the questionnaire
    ){

    companion object {
        // Idea is that we don't need to define a route and requests in strings. Any changes only need to come from the models
        // then the client and the server are adjusted automatically
        const val path = "/questionnaire"
    }
}