import com.benjtissot.sellingmugs.entities.openAI.CategoriesChatRequestParams
import com.benjtissot.sellingmugs.entities.openAI.CustomStatusCode
import com.benjtissot.sellingmugs.entities.openAI.GenerateCategoriesStatus
import com.benjtissot.sellingmugs.entities.openAI.OpenAIUnavailable
import com.benjtissot.sellingmugs.services.ImageGeneratorService
import io.ktor.util.logging.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.fail
import kotlin.time.Duration.Companion.seconds

class ChatGPTTests : AbstractDatabaseTests() {

    @Test
    fun chatGPTGenerationTest() = runTest(timeout = 180.seconds) {
        launch {
            val params = CategoriesChatRequestParams(5, 2, null, false)
            try {
                LOG.debug("Generating categories")
                val categoriesStatus = ImageGeneratorService.generateCategoriesAndMugs(params)
                // TODO: find a good way to test this
                delay(100*1000L)
                LOG.debug("Success percentage was ${calculateSuccessPercentage(categoriesStatus!!)}%")
                assert(calculateSuccessPercentage(categoriesStatus) >= 75)
            } catch (e: OpenAIUnavailable) {
                e.printStackTrace()
                LOG.error("Try again, OpenAI was unavailable")
            } catch(e: Exception) {
                fail(e.message)
            }
        }
    }

    companion object {

        private val LOG = KtorSimpleLogger("ChatGPTTests.kt")

        private fun calculateSuccessPercentage(categoriesStatus : GenerateCategoriesStatus) : Int {
            return (categoriesStatus.statuses.sumOf { calculateSuccessPercentage(it.customStatusCodes) }.toFloat() / categoriesStatus.statuses.size.toFloat()).toInt()
        }

        private fun calculateSuccessPercentage(statusCodes : List<CustomStatusCode>) : Int {
            if (statusCodes.isEmpty()) return 0
            return (statusCodes.filter { it.value == 200 }.size.toFloat() / statusCodes.size.toFloat() * 100f).toInt()
        }

    }


}