import com.benjtissot.sellingmugs.entities.Session
import com.benjtissot.sellingmugs.entities.User
import com.benjtissot.sellingmugs.repositories.SessionRepository
import com.benjtissot.sellingmugs.repositories.UserRepository
import com.benjtissot.sellingmugs.services.LoginService
import com.benjtissot.sellingmugs.services.SessionService
import io.ktor.util.logging.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class LoginTests : AbstractDatabaseTests() {

    private val LOG = KtorSimpleLogger("LoginTests.kt")

    lateinit var session: Session
    lateinit var user: User

    @Before
    fun setup(){
        user = User.dummy(2)
    }

    @Test
    /**
     * Creates a user and a session, checks that the user is logged into the session and that
     * all the correct data has been saved to the database
     */
    fun registerTest() = runTest {
        launch {
            session = SessionRepository.createSession()
            LOG.info("Registering user ${user.getNameInitial()}")
            LoginService.register(user, session)
        }
    }

}