package com.benjtissot.sellingmugs.entities.openAI

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

open class OpenAIError(override val message: String) : Exception()

class OpenAIUnavailable(): OpenAIError("Open AI server is unavailable, try later please")


