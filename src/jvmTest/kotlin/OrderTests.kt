import com.benjtissot.sellingmugs.entities.RegisterInfo
import com.benjtissot.sellingmugs.entities.Session
import com.benjtissot.sellingmugs.repositories.SessionRepository
import com.benjtissot.sellingmugs.repositories.UserRepository
import com.benjtissot.sellingmugs.services.BadCredentialsException
import com.benjtissot.sellingmugs.services.LoginService
import com.benjtissot.sellingmugs.services.UserAlreadyExistsException
import io.ktor.util.logging.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFailsWith
import kotlin.test.fail

class OrderTests : AbstractDatabaseTests() {

    private val LOG = KtorSimpleLogger("OrderTests.kt")

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
     * Creates an order in database and in printify
     */
    fun createOrderFromCart() = runTest {
        LOG.delimit("Register Success Test")
        launch {

        }
    }

}