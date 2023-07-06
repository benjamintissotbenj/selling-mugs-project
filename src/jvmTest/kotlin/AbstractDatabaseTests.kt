import ch.qos.logback.classic.LoggerContext
import io.ktor.util.logging.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import org.slf4j.LoggerFactory
import kotlin.random.Random

val random = Random(0)
abstract class AbstractDatabaseTests {

    @Before
    open fun before(){
        LOG.delimit("Test preparation start")
        clearDatabase()
    }

    @After
    open fun after(){
        LOG.delimit("Test end")
    }

    /**
     * Clears the database, called before every test
     */
    private fun clearDatabase() = runTest {
        // Start off by clearing the test database
        // Doing this here rather than in the After to be able to look at the state of the database
        // after testing
        launch {
            LOG.warn("Dropping every collection in test database")
            database.listCollectionNames().forEach {
                database.dropCollection(it)
            }
        }
    }

    companion object {

        private val LOG = KtorSimpleLogger("AbstractDatabaseTests.kt")
        @OptIn(DelicateCoroutinesApi::class)
        val mainThreadSurrogate = newSingleThreadContext("Test-coroutine-thread-${random.nextInt()}")

        @BeforeClass
        @JvmStatic fun init() {
            LOG.delimit("START INIT")
            deactivateMongoDriverLogs()

            setupScope()
            database = client.getDatabase(System.getenv("MONGODB_DBNAME") ?: "test")
            LOG.delimit("FINISH INIT")
        }

        @OptIn(ExperimentalCoroutinesApi::class)
        @AfterClass
        @JvmStatic fun teardown() {
            LOG.delimit("START TEARDOWN")
            Dispatchers.resetMain() // reset the main dispatcher to the original Main dispatcher
            LOG.delimit("FINISH TEARDOWN")
        }

        private fun deactivateMongoDriverLogs(){
            // Deactivating MongoDb Driver logs
            val loggerContext: LoggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
            val rootLogger = loggerContext.getLogger("org.mongodb.driver")
            rootLogger.level = ch.qos.logback.classic.Level.OFF

            LOG.warn("MongoDB Driver Logs deactivated")
        }

        @OptIn(ExperimentalCoroutinesApi::class)
        private fun setupScope(){
            LOG.warn("Setting up a surrogate main thread, \"Test coroutine thread\"")
            Dispatchers.setMain(mainThreadSurrogate)
        }
    }
}