package com.benjtissot.sellingmugs.repositories

import com.benjtissot.sellingmugs.entities.User
import com.benjtissot.sellingmugs.entities.printify.order.*
import database
import org.litote.kmongo.*

val orderCollection = database.getCollection<Order>()
val userOrderListCollection = database.getCollection<UserOrderList>()
val orderPushSuccessCollection = database.getCollection<StoredOrderPushSuccess>()
val orderPushFailedCollection = database.getCollection<StoredOrderPushFailed>()

class OrderRepository {
    companion object {

        /**
         * Retrieves a user's list of orders
         * @param userId the id of the [User] for which to retrieve the list of past [Order]s
         * @return a [UserOrderList] if the list exists, null otherwise (shouldn't happen, but we never know)
         */
        suspend fun getUserOrderListByUserId(userId: String) : UserOrderList? {
            return userOrderListCollection.findOneById(userId)
        }

        /**
         * Adds an [Order.external_id] to a [User]'s list of orders
         * @param userId the id of the [User] for which to retrieve the list of past [Order]s
         * @param orderId the local id of the [Order] to be added to the list
         */
        suspend fun addOrderToUserOrderList(userId: String, orderId: String){
            userOrderListCollection.updateOneById(userId, push(UserOrderList::orderIds, orderId), upsert())
        }

        /**
         * Creates a user's list of orders in the database
         * @param userOrderList the item to insert
         */
        suspend fun insertUserOrderList(userOrderList: UserOrderList){
            userOrderListCollection.insertOne(userOrderList)
        }


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

        /**
         * @param localOrderId the [Order.external_id] for which we want to retrieve the stored push result
         */
        suspend fun getOrderPushResultByOrderId(localOrderId : String) : PrintifyOrderPushResult? {
            return (
                    orderPushSuccessCollection.findOne(StoredOrderPushSuccess::orderId eq localOrderId)?.printifyOrderPushSuccess
                        ?: orderPushFailedCollection.findOne(StoredOrderPushFailed::orderId eq localOrderId)?.printifyOrderPushFail
                    )
        }

        /**
         * @param localOrderId the [Order.external_id] for which we want to store the push result
         * @param printifyOrderPushResult the [PrintifyOrderPushResult] to be stored
         */
        suspend fun saveOrderPushResult(localOrderId: String, printifyOrderPushResult: PrintifyOrderPushResult) {
            if (printifyOrderPushResult is PrintifyOrderPushSuccess) {
                orderPushSuccessCollection.updateOneById(
                    localOrderId,
                    StoredOrderPushSuccess(localOrderId, printifyOrderPushResult),
                    upsert()
                )
            } else if (printifyOrderPushResult is PrintifyOrderPushFail) {
                orderPushFailedCollection.updateOneById(
                    localOrderId,
                    StoredOrderPushFailed(localOrderId, printifyOrderPushResult),
                    upsert()
                )
            }

        }

    }
}