import ch.qos.logback.classic.LoggerContext
import io.ktor.util.logging.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.AfterClass
import org.junit.BeforeClass
import org.slf4j.LoggerFactory

abstract class AbstractDatabaseTests {
    companion object {

        private val LOG = KtorSimpleLogger("AbstractDatabaseTests.kt")
        @OptIn(DelicateCoroutinesApi::class)
        val mainThreadSurrogate = newSingleThreadContext("Test coroutine thread")

        @BeforeClass
        @JvmStatic fun setup() {
            LOG.delimit("START SETUP")
            deactivateMongoDriverLogs()

            setupScope()

            database = client.getDatabase("test")

            clearDatabase()
            LOG.delimit("FINISH SETUP")
        }

        @OptIn(ExperimentalCoroutinesApi::class)
        @AfterClass
        @JvmStatic fun teardown() {
            LOG.delimit("START TEARDOWN")
            client.close()
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

        @OptIn(ExperimentalCoroutinesApi::class)
        private fun setupScope(){
            LOG.warn("Setting up a surrogate main thread, \"Test coroutine thread\"")
            Dispatchers.setMain(mainThreadSurrogate)
        }
    }
}