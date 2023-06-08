import ch.qos.logback.classic.LoggerContext
import com.mongodb.reactivestreams.client.MongoClient
import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import org.slf4j.LoggerFactory

abstract class AbstractDatabaseTests : AbstractCoroutineTests() {
    companion object {

        private lateinit var mongoClient: MongoClient
        lateinit var database: CoroutineDatabase

        @BeforeClass
        @JvmStatic fun setup() {
            mongoClient = KMongo.createClient()
            database = mongoClient.coroutine.getDatabase("test")

            // Deactivating MongoDb Driver logs
            val loggerContext: LoggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
            val rootLogger = loggerContext.getLogger("org.mongodb.driver")
            rootLogger.level = ch.qos.logback.classic.Level.OFF

            val LOG = java.util.logging.Logger.getLogger(this.javaClass.name)
            LOG.severe("MongoDB Driver Logs deactivated")
        }

        @AfterClass
        @JvmStatic fun teardown() {
            mongoClient.coroutine.close()
            mongoClient.close()
        }
    }
}