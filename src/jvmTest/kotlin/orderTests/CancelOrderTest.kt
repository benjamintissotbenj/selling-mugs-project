package orderTests

import AbstractDatabaseTests
import com.benjtissot.sellingmugs.entities.Session
import com.benjtissot.sellingmugs.repositories.SessionRepository
import delimit
import io.ktor.util.logging.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class CancelOrderTest : AbstractDatabaseTests() {

    private val LOG = KtorSimpleLogger("orderTests/deleteOrderTests.kt")

    lateinit var session: Session
    val orderId = "999999"


    @Before
    override fun before() = runTest {
        super.before()
        launch {
            session = SessionRepository.createSession()
        }
    }

    @Test
    /**
     * Cancels an order in Printify
     */
    fun deleteOrder() = runTest {
        LOG.delimit("Delete Order Test")
        launch {

        }
    }

}