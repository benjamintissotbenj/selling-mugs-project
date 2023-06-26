package com.benjtissot.sellingmugs.entities.printify.order

import com.benjtissot.sellingmugs.ORDER_OBJECT_PATH
import io.ktor.http.*
import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import com.benjtissot.sellingmugs.entities.printify.customParseToInstant
import kotlinx.datetime.Clock

fun Instant.customParse(isoString: String) : Instant {
    return Instant.parse(isoString.replace(" ", "T"))
}
object InstantSerializer : KSerializer<Instant> {
    override val descriptor = PrimitiveSerialDescriptor("Instant", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: Instant) = encoder.encodeString(value.toString())
    override fun deserialize(decoder: Decoder): Instant = customParseToInstant(decoder.decodeString())
}

@Serializable
data class Order(
    @SerialName("_id") val external_id: String, // parameter name for json serialisation
    val label: String,
    val line_items: List<LineItem>,
    val address_to: AddressTo,
    val shipping_method: Int, // 1 is standard, 2 is express
    val send_shipping_notification: Boolean,
    val id: String, // Printify id
    val status: String,
    @Serializable(InstantSerializer::class)
    val created_at: Instant,
) {
    companion object {
        const val STATUS_CANCELLED = "canceled" // NOT a typo, Printify status has a typo so must adapt
        const val STATUS_ON_HOLD = "on-hold"
        const val STATUS_PENDING = "pending"
        const val STATUS_PAYMENT_NOT_RECEIVED = "payment-not-received"

        const val path = ORDER_OBJECT_PATH

        // Needed outside the constructor for serialisation issues
        fun create(external_id: String, label: String, line_items: List<LineItem>, address_to: AddressTo,) : Order {
            return Order(
                external_id, label, line_items, address_to,
                shipping_method = 1, // 1 is standard, 2 is express
                send_shipping_notification = true,
                id = "", // Printify id
                status = STATUS_ON_HOLD, // Order is created on hold and published as pending
                Clock.System.now()
            )
        }
    }
    fun getCalculateShipping(): OrderToCalculateShippingCosts {
        return OrderToCalculateShippingCosts(line_items, address_to)
    }
}


@Serializable
data class UserOrderList (
    @SerialName("_id") val userId: String,
    val orderIds: List<String>,
){

}


@Serializable
data class ReceiveOrder(
    val id: String, // Printify id
    val status: String,
    @Serializable(InstantSerializer::class)
    val created_at: Instant,
){

}

@Serializable
data class OrderToCalculateShippingCosts(
    val line_items: List<LineItem>,
    val address_to: AddressTo,
) {
}

@Serializable
data class ShippingCosts(
    val standard: Int,
    val express: Int,
) {
}
object PushResultSerializer : JsonContentPolymorphicSerializer<PrintifyOrderPushResult>(PrintifyOrderPushResult::class) {
    override fun selectDeserializer(element: JsonElement) = when {
        "id" in element.jsonObject -> PrintifyOrderPushSuccess.serializer()
        else -> PrintifyOrderPushFail.serializer()
    }
}
@Polymorphic
interface PrintifyOrderPushResult {

}

@Serializable
data class PrintifyOrderPushSuccess (
    val id: String,
) : PrintifyOrderPushResult {
}

@Serializable
data class PrintifyOrderPushFail (
    val status: String,
    val code: Int,
    val message: String,
    val errors: PrintifyOrderPushFailError
) : PrintifyOrderPushResult {
    companion object {
        val notFoundInDatabase = PrintifyOrderPushFail(
            "error",
            400,
            "Order was not found in local database",
            PrintifyOrderPushFailError("{\"database\":[\"Order was not found in local database.\"]}", 400)
        )
    }
}

@Serializable
data class PrintifyOrderPushFailError (
    val reason: String,
    val code: Int
) {
}

@Serializable
class StoredOrderPushSuccess (
    @SerialName("_id") val orderId: String,
    val printifyOrderPushSuccess: PrintifyOrderPushSuccess
) {

}

@Serializable
class StoredOrderPushFailed (
    @SerialName("_id") val orderId: String,
    val printifyOrderPushFail: PrintifyOrderPushFail
) {

}