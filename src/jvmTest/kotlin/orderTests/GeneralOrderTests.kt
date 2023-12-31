package orderTests

import AbstractDatabaseTests
import com.benjtissot.sellingmugs.entities.local.LoginInfo
import com.benjtissot.sellingmugs.entities.local.RegisterInfo
import com.benjtissot.sellingmugs.entities.local.Session
import com.benjtissot.sellingmugs.entities.printify.order.AddressTo
import com.benjtissot.sellingmugs.entities.printify.order.Order
import com.benjtissot.sellingmugs.entities.printify.order.PrintifyOrderPushSuccess
import com.benjtissot.sellingmugs.repositories.CartRepository
import com.benjtissot.sellingmugs.repositories.SessionRepository
import com.benjtissot.sellingmugs.services.*
import delimit
import imageForUpload1
import imageForUpload2
import imageForUpload3
import io.ktor.http.*
import io.ktor.util.logging.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.fail
import kotlin.time.Duration.Companion.seconds

class GeneralOrderTests : AbstractDatabaseTests() {

    private val LOG = KtorSimpleLogger("orderTests/GeneralOrderTests.kt")

    lateinit var session: Session
    lateinit var orderId : String
    val productIds : ArrayList<String> = ArrayList(emptyList())


    @Before
    override fun before() = runTest(timeout = 30.seconds) {
        super.before()
        launch {
            session = SessionRepository.createSession()
            LoginService.register(
                RegisterInfo("Test", "TEST", "123", "123"),
                session
            )
            session = LoginService.login(
                LoginInfo("123", "123"),
                session
            )
            // Create 3 different mugs
            LOG.debug("Creating 3 different mugs")
            productIds.add(CreateOrderTest.createProductWithImage(imageForUpload1))
            productIds.add(CreateOrderTest.createProductWithImage(imageForUpload2))
            productIds.add(CreateOrderTest.createProductWithImage(imageForUpload3))

            // Add the products to cart
            CartService.getCart(session.cartId)?.let { cart ->
                productIds.forEach { printifyId ->
                    MugService.getMugByPrintifyId(printifyId)?.let { mug ->
                        CartService.addMugToCart(mug, cart)
                    }
                }
            }
            val addressTo = AddressTo(
                "Test",
                "TEST",
                "selling.mugs.imperial@gmail.com",
                "",
                "GB",
                "England",
                "Exhibition Rd",
                "South Kensington",
                "London",
                "SW7 2BX"
            )
            session.user?.let {
                orderId = OrderService.createOrderFromCart(addressTo, session.cartId, it, testOrder = true).external_id
                LOG.debug("The local OrderID is $orderId")

                session = SessionRepository.updateSession(
                    session.copy(orderId = orderId,
                        cartId = CartRepository.createCart().id)
                )
            }

        }
    }

    @Test
    /**
     * Creates an order for Printify
     */
    fun placeOrderToPrintifyTest() = runTest {
        LOG.delimit("placeOrderToPrintify Test")
        launch {
            val printifyOrderPushResult = OrderService.placeOrderToPrintify(orderId)
            if (printifyOrderPushResult is PrintifyOrderPushSuccess){
                LOG.debug("The printify OrderID is ${printifyOrderPushResult.id}")
                val order = OrderService.getOrderFromPrintify(orderId)
                assert(order != null)
                order?.id?.let {
                    assert(it.isNotBlank())
                    assert(it == printifyOrderPushResult.id)// assert that printify id is not null
                }
                // Assert that the order is placed on-hold (hasn't been placed to printify)
                assert(order?.status == Order.STATUS_PENDING)
            } else {
                fail()
            }
        }
    }

    @Test
    /**
     * Cancels an order in Printify
     */
    fun cancelOrder() = runTest {
        LOG.delimit("Cancel Order Test")
        launch {
            OrderService.placeOrderToPrintify(orderId)

            val responseCode = OrderService.cancelOrder(orderId)
            assert(responseCode == HttpStatusCode.OK)

            val order = OrderService.getOrderFromPrintify(orderId)

            delay(200L)
            // Check order is cancelled
            assert(order?.status == Order.STATUS_CANCELLED)
        }
    }


    @After
    override fun after() = runTest {
        super.after()
        launch {
            // Cancels the order created in printify
            LOG.debug("Cancelling order $orderId")
            OrderService.cancelOrder(orderId)
            productIds.forEach {
                // Delete the published product if it has been published
                LOG.debug("Deleting product $it")
                PrintifyService.deleteProduct(it)
            }
        }
    }

}