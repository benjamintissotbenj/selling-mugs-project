package orderTests

import AbstractDatabaseTests
import com.benjtissot.sellingmugs.entities.Session
import com.benjtissot.sellingmugs.entities.printify.order.AddressTo
import com.benjtissot.sellingmugs.repositories.SessionRepository
import com.benjtissot.sellingmugs.services.OrderService
import delimit
import io.ktor.util.logging.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class EmptyOrderTest : AbstractDatabaseTests() {

    private val LOG = KtorSimpleLogger("orderTests/deleteOrderTests.kt")

    lateinit var session: Session
    lateinit var emptyOrderId : String
    lateinit var emptyOrderPrintifyId : String


    @Before
    override fun before() = runTest {
        super.before()
        launch {
            session = SessionRepository.createSession()
            val addressTo = AddressTo(
                "Test",
                "TEST",
                "selling.mugs.imperial@gmail.com",
                "",
                "UK",
                "",
                "Exhibition Rd",
                "South Kensington",
                "London",
                "SW7 2BX"
            )
            emptyOrderId = OrderService.createOrderFromCart(addressTo).external_id
        }
    }

    @Test
    /**
     * Cancels an order in Printify. Order is empty to ensure nothing is actually ordered at any given time
     */
    fun sendOrderToProduction() = runTest {
        LOG.delimit("Delete Order Test")
        launch {
            OrderService.placeOrderToPrintify(emptyOrderId)
            val order = OrderService.getOrderFromPrintify(emptyOrderId)

            // Check order is empty
            assert(order?.line_items?.isEmpty() ?: false)
            OrderService.sendOrderToProduction(emptyOrderId)

            // Check order is put into production
            assert(order?.status == "payment-not-received")
        }
    }

    @Test
    /**
     * Cancels an order in Printify. Order is empty to ensure nothing is actually ordered at any given time
     */
    fun deleteOrder() = runTest {
        LOG.delimit("Delete Order Test")
        launch {
            OrderService.cancelOrder(emptyOrderId)
            val order = OrderService.getOrderFromPrintify(emptyOrderId)

            // Check order is empty
            assert(order?.line_items?.isNotEmpty() ?: false)

            // Check order is cancelled
            assert(order?.status == "cancelled")
        }
    }

}