package orderTests

import AbstractDatabaseTests
import com.benjtissot.sellingmugs.entities.Session
import com.benjtissot.sellingmugs.entities.printify.ImageForUpload
import com.benjtissot.sellingmugs.entities.printify.order.AddressTo
import com.benjtissot.sellingmugs.repositories.SessionRepository
import com.benjtissot.sellingmugs.services.CartService
import com.benjtissot.sellingmugs.services.MugService
import com.benjtissot.sellingmugs.services.OrderService
import com.benjtissot.sellingmugs.services.PrintifyService
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

class CreateOrderTest : AbstractDatabaseTests() {

    private val LOG = KtorSimpleLogger("orderTests/CreateOrderTests.kt")

    lateinit var session: Session
    val productIds : ArrayList<String> = ArrayList(emptyList())


    @Before
    override fun before() = runTest {
        super.before()
        launch {
            session = SessionRepository.createSession()
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
            val orderId = OrderService.createOrderFromCart(addressTo, session.cartId).external_id

            // Assert the order has been created in the database
            val order = OrderService.getOrder(orderId)
            assert(order != null)
            // Assert that the line items are created correctly
            assert(order?.line_items?.map {it.product_id} == productIds)
            // Assert that the order is created on hold
            assert(order?.status == "on-hold")
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