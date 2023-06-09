import com.benjtissot.sellingmugs.entities.RegisterInfo
import com.benjtissot.sellingmugs.entities.Session
import com.benjtissot.sellingmugs.repositories.SessionRepository
import com.benjtissot.sellingmugs.repositories.UserRepository
import com.benjtissot.sellingmugs.services.BadCredentialsException
import com.benjtissot.sellingmugs.services.LoginService
import io.ktor.util.logging.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.fail

class LoginTests : AbstractDatabaseTests() {

    private val LOG = KtorSimpleLogger("LoginTests.kt")

    lateinit var session: Session
    var registerInfos = ArrayList<RegisterInfo>(emptyList<RegisterInfo>()) // list of 10 users, 2 admins


    @Before
    override fun before() = runTest {
        super.before()
        for (i in 0 until 10){
            registerInfos.add(RegisterInfo.dummy(i))
        }
        launch {
            session = SessionRepository.createSession()
        }
    }

    @Test
    /**
     * Checks that the user is registered, then logged into the session and that
     * all the correct data has been saved to the database
     */
    fun registerSuccessTest() = runTest {
        LOG.delimit("Register Success Test")
        launch {
            LOG.info("Registering user ${2}")
            session = LoginService.register(registerInfos[2], session)
            val getUser = UserRepository.getUserByEmail("${2}")
            assert(getUser != null)
            assert(getUser?.id != null)
            assert(getUser?.id == session.user?.id)
        }
    }

    @Test
    /**
     * Logs in a user, then Checks that the user is logged into the session and that
     * all the correct data has been saved to the database
     */
    fun loginSuccessTest() = runTest {
        launch {
            LoginService.register(registerInfos[2], session)
            try {
                LOG.info("Logging in user ${2}")
                session = LoginService.login(registerInfos[2].toLoginInfo(), session)
            } catch (e: BadCredentialsException){
                fail("Bad Credentials")
            }
            val getUser = UserRepository.getUserByEmail("${2}")
            assert(getUser != null)
            assert(getUser?.id != null)
            assert(getUser?.id == session.user?.id)
        }
    }

}