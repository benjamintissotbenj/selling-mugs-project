package orderTests

import AbstractDatabaseTests
import com.benjtissot.sellingmugs.entities.LoginInfo
import com.benjtissot.sellingmugs.entities.RegisterInfo
import com.benjtissot.sellingmugs.entities.Session
import com.benjtissot.sellingmugs.entities.printify.ImageForUpload
import com.benjtissot.sellingmugs.entities.printify.order.AddressTo
import com.benjtissot.sellingmugs.entities.printify.order.Order
import com.benjtissot.sellingmugs.repositories.CartRepository
import com.benjtissot.sellingmugs.repositories.OrderRepository
import com.benjtissot.sellingmugs.repositories.SessionRepository
import com.benjtissot.sellingmugs.repositories.UserRepository
import com.benjtissot.sellingmugs.services.*
import delimit
import imageForUpload1
import imageForUpload2
import imageForUpload3
import io.ktor.http.*
import io.ktor.util.logging.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.asserter
import kotlin.test.fail
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.Duration.Companion.seconds

class CreateOrderTest : AbstractDatabaseTests() {

    private val LOG = KtorSimpleLogger("orderTests/CreateOrderTests.kt")

    lateinit var session: Session
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
            productIds.add(createProductWithImage(imageForUpload1))
            productIds.add(createProductWithImage(imageForUpload2))
            productIds.add(createProductWithImage(imageForUpload3))

            // Add the products to cart
            CartService.getCart(session.cartId)?.let { cart ->
                productIds.forEach { printifyId ->
                    MugService.getMugByPrintifyId(printifyId)?.let { mug ->
                        CartService.addMugToCart(mug, cart)
                    }
                }
            }

        }
    }

    @OptIn(ExperimentalTime::class)
    @Test
    /**
     * Creates an order in database
     */
    fun createOrderFromCart() = runTest {
        LOG.delimit("CreateOrder Test")
        launch {
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

            session.user?.let { user ->
                val orderId = OrderService.createOrderFromCart(addressTo, session.cartId, user).external_id
                session = SessionRepository.updateSession(
                    session.copy(orderId = orderId,
                        cartId = CartRepository.createCart().id)
                )
                // Assert the order has been created in the database
                val order = OrderService.getOrder(orderId)
                assert(order != null)
                // Assert that the line items are created correctly
                assert(order?.line_items?.map {it.product_id} == productIds)
                // Assert that the order is created pending
                assert(order?.status == Order.STATUS_PENDING)
                // Assert the orderID has been added to the user and the session
                assert(session.user?.id?.let { OrderRepository.getUserOrderListByUserId(it)?.orderIds?.contains(orderId) } ?: false)
                assert(session.orderId == orderId)
            } ?: fail()

        }
    }

    @After
    override fun after() = runTest {
        launch {
            productIds.forEach {
                // Delete the published product if it has been published
                LOG.debug("Deleting product $it")
                PrintifyService.deleteProduct(it)
            }
            super.after()
        }
    }

    companion object {

        suspend fun createProductWithImage(imageForUpload: ImageForUpload) : String {

            // Upload Image
            val imageForUploadReceive = PrintifyTests.uploadImageTest(imageForUpload)

            // Create Product
            val productId = PrintifyTests.createProductTest(imageForUploadReceive)

            // Publish Product if productId is not null
            PrintifyTests.publishProductTest(productId)

            return productId
        }
    }

}