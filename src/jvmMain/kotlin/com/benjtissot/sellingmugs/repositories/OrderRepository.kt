package com.benjtissot.sellingmugs.repositories

import com.benjtissot.sellingmugs.entities.Cart
import com.benjtissot.sellingmugs.entities.printify.order.Order
import com.benjtissot.sellingmugs.genUuid
import database
import org.litote.kmongo.MongoOperator
import org.litote.kmongo.and
import org.litote.kmongo.eq
import org.litote.kmongo.upsert

val orderCollection = database.getCollection<Order>()

class OrderRepository {
    companion object {
        
        suspend fun getOrder(id: String) : Order? {
            return orderCollection.findOne(Order::external_id eq id)
        }

        /**
         * @param order the [Order] to be inserted
         */
        suspend fun insertOrder(order: Order) {
            orderCollection.insertOne(order)
        }

        /**
         * @param order the [Order] to be updated (inserted if not existent)
         */
        suspend fun updateOrder(order: Order) {
            orderCollection.updateOneById(order.external_id, order, upsert())
        }

    }
}