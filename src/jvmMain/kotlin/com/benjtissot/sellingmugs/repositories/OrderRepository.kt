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


        /**
         * @param id local id of the [Order] to get
         */
        suspend fun getOrder(id: String) : Order? {
            return orderCollection.findOne(Order::external_id eq id)
        }

        /**
         * @param id printify id of the [Order] to get
         */
        suspend fun getOrderByPrintifyId(printifyId: String) : Order? {
            return orderCollection.findOne(Order::id eq printifyId)
        }

        /**
         * @return the next label when an order is created
         */
        suspend fun getOrderNextLabel() : String {
            return "${orderCollection.countDocuments()}"
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

        /**
         * @param localId the local id for which to retrieve the printify id of the order
         */
        suspend fun getOrderPrintifyId(localId: String) : String {
            return orderCollection.findOne(Order::external_id eq localId)?.id ?: ""
        }

    }
}