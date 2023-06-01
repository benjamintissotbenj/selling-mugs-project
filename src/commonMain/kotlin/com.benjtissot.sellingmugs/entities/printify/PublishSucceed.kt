package com.benjtissot.sellingmugs.entities.printify

import kotlinx.serialization.Serializable
@Serializable
class PublishSucceed(val external: External) {
}

@Serializable
class External(val id: String, val handle: String) {

}