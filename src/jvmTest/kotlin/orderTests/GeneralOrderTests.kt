package orderTests

import AbstractDatabaseTests
import com.benjtissot.sellingmugs.entities.Session
import com.benjtissot.sellingmugs.entities.printify.order.AddressTo
import com.benjtissot.sellingmugs.entities.printify.order.PrintifyOrderPushSuccess
import com.benjtissot.sellingmugs.repositories.SessionRepository
import com.benjtissot.sellingmugs.services.CartService
import com.benjtissot.sellingmugs.services.MugService
import com.benjtissot.sellingmugs.services.OrderService
import delimit
import imageForUpload1
import imageForUpload2
import imageForUpload3
import io.ktor.util.logging.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.fail

class GeneralOrderTests : AbstractDatabaseTests() {

    private val LOG = KtorSimpleLogger("orderTests/GeneralOrderTests.kt")

    lateinit var session: Session
    lateinit var orderId : String
    val productIds : ArrayList<String> = ArrayList(emptyList())


    @Before
    override fun before() = runTest {
        super.before()
        launch {
            session = SessionRepository.createSession()
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
                "UK",
                "",
                "Exhibition Rd",
                "South Kensington",
                "London",
                "SW7 2BX"
            )
            orderId = OrderService.createOrderFromCart(addressTo).external_id
        }
    }

    @Test
    /**
     * Calculates shipping price for order
     */
    fun calculateShippingPrice() = runTest {
        LOG.delimit("Calculate shipping price Test")
        launch {
            val shippingCosts = OrderService.calculateOrderShippingCost(orderId)
            assert(shippingCosts != null)
            // TODO: Assert correct prices
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
                val order = OrderService.getOrderFromPrintify(orderId)
                assert(order != null)
                order?.id?.let {
                    assert(it.isNotBlank())
                    assert(it == printifyOrderPushResult.id)
                } // assert that printify id is not null
            } else {
                fail()
            }
        }
    }

}