import com.benjtissot.sellingmugs.entities.local.Artwork
import com.benjtissot.sellingmugs.entities.local.Mug
import com.benjtissot.sellingmugs.entities.local.MugFilter
import com.benjtissot.sellingmugs.entities.local.Session
import com.benjtissot.sellingmugs.genUuid
import com.benjtissot.sellingmugs.repositories.SessionRepository
import com.benjtissot.sellingmugs.services.MugService
import io.ktor.util.logging.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class MugListTests : AbstractDatabaseTests() {

    private val LOG = KtorSimpleLogger("LoginTests.kt")

    private lateinit var session: Session
    private val categoriesAmount = 4
    private val mugsAmountPerCategory = 10


    @Before
    override fun before() = runTest {
        super.before()
        launch {
            session = SessionRepository.createSession()
            for (j: Int in 0 until categoriesAmount){
                for (i: Int in 1..mugsAmountPerCategory){
                    val artwork = Artwork(genUuid(), imageURL = "", previewURLs = emptyList())
                    val mug = Mug(genUuid(), "", "Mug ${j*mugsAmountPerCategory + i}", "Description", 7.2f, "Category $j", artwork)
                    MugService.insertNewMug(mug)
                }
            }
        }
    }

    @Test
    /**
     * Checks that the user is registered, then logged into the session and that
     * all the correct data has been saved to the database
     */
    fun getMugListTest() = runTest {
        LOG.delimit("Get Mug List Test")
        launch {
            val allMugs = MugService.getAllMugsList()
            assert(allMugs.size == categoriesAmount*mugsAmountPerCategory)
            val allMugsPaginated = MugService.getAllMugsList(MugFilter(currentPage = 0))
            assert(allMugsPaginated.size == MugService.mugsPerPage)
            val allMugsPaginated2 = MugService.getAllMugsList(MugFilter(currentPage = 1))
            assert(allMugsPaginated2[0].name == "Mug 26")

            val allMugsCategoryFilteredPaginated = MugService.getAllMugsList(MugFilter(currentPage = 0, categories = listOf("Category 0","Category 1","Category 2")))
            assert(allMugsCategoryFilteredPaginated.size <= MugService.mugsPerPage)
            assert(allMugsCategoryFilteredPaginated[0].category == "Category 0")
            assert(allMugsCategoryFilteredPaginated[allMugsCategoryFilteredPaginated.size-1].category == "Category 2") // 25th mug is 3rd category

            val allMugsCategoryFilteredPaginated2 = MugService.getAllMugsList(MugFilter(currentPage = 1, categories = listOf("Category 0","Category 1","Category 2")))
            assert(allMugsCategoryFilteredPaginated2.size == 5) //30 mugs of these 3 categories, 2nd page has 5 mugs left
            assert(allMugsCategoryFilteredPaginated2[0].category == "Category 2")
        }
    }

}