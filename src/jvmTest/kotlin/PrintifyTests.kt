import com.benjtissot.sellingmugs.Const
import com.benjtissot.sellingmugs.entities.printify.ImageForUpload
import com.benjtissot.sellingmugs.entities.printify.ImageForUploadReceive
import com.benjtissot.sellingmugs.entities.printify.MugProductInfo
import com.benjtissot.sellingmugs.services.MugService
import com.benjtissot.sellingmugs.services.PrintifyService
import io.ktor.http.*
import io.ktor.util.logging.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.time.Duration.Companion.seconds

class PrintifyTests : AbstractDatabaseTests() {

    @Test
    fun managingProduct() = runTest(timeout = 30.seconds) {
        LOG.debug("Information")
        launch {
            LOG.debug("Information in coroutine")
            // Upload Image
            val imageForUploadReceive = uploadImageTest(imageForUpload1)

            // Create Product
            val productId = createProductTest(imageForUploadReceive)

            // Publish Product if productId is not null
            publishProductTest(productId)

            // Delete the published product if it has been published
            val deletedCode = PrintifyService.deleteProduct(productId)
            assert(deletedCode == HttpStatusCode.OK)
            assert(PrintifyService.getProduct(productId) == null)
            assert(MugService.getMugByPrintifyId(productId) == null)
        }
    }

    companion object {

        private val LOG = KtorSimpleLogger("PrintifyTests.kt")
        suspend fun uploadImageTest(imageForUpload: ImageForUpload) : ImageForUploadReceive {

            val imageForUploadReceive = PrintifyService.uploadImage(imageForUpload, true)
            LOG.debug("ImageReceived is $imageForUploadReceive")
            assert(imageForUploadReceive != null)
            return imageForUploadReceive!!
        }

        suspend fun createProductTest(imageForUploadReceive : ImageForUploadReceive) : String {
            val mugProductInfo = MugProductInfo("Test Title", "Test product upload Description", Const.mugCategoryDefault, imageForUploadReceive!!.toImage())
            val productId = PrintifyService.createProduct(mugProductInfo)
            LOG.debug("productId after product creation is $productId")
            assert(productId != null)
            return productId!!
        }

        suspend fun publishProductTest(productId: String) {
            val publishedCode = PrintifyService.publishProduct(productId)
            LOG.debug("publishedCode after product publication is $publishedCode")
            assert(publishedCode == HttpStatusCode.OK)
        }
    }


}