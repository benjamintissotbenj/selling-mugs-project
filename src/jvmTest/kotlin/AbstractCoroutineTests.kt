import ch.qos.logback.classic.LoggerContext
import com.mongodb.reactivestreams.client.MongoClient
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import org.slf4j.LoggerFactory

abstract class AbstractCoroutineTests {
    companion object {
        @OptIn(DelicateCoroutinesApi::class)
        val mainThreadSurrogate = newSingleThreadContext("Test coroutine thread")

        @OptIn(ExperimentalCoroutinesApi::class)
        @BeforeClass
        @JvmStatic
        fun setup() {
            val LOG = java.util.logging.Logger.getLogger(this.javaClass.name)
            LOG.severe("Setting a surrogate main thread, \"Test corouting thread \"")
            Dispatchers.setMain(mainThreadSurrogate)
        }

        @OptIn(ExperimentalCoroutinesApi::class)
        @AfterClass
        @JvmStatic
        fun teardown() {
            Dispatchers.resetMain() // reset the main dispatcher to the original Main dispatcher
        }
    }
}