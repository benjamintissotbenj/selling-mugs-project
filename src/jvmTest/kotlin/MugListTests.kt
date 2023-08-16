import com.benjtissot.sellingmugs.Const
import com.benjtissot.sellingmugs.entities.local.*
import com.benjtissot.sellingmugs.genUuid
import com.benjtissot.sellingmugs.getUuidFromString
import com.benjtissot.sellingmugs.repositories.CategoryRepository
import com.benjtissot.sellingmugs.repositories.MugRepository
import com.benjtissot.sellingmugs.repositories.SessionRepository
import com.benjtissot.sellingmugs.services.CategoryService
import com.benjtissot.sellingmugs.services.MugService
import io.ktor.util.logging.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.Clock

class MugListTests : AbstractDatabaseTests() {

    private val LOG = KtorSimpleLogger("LoginTests.kt")

    private lateinit var session: Session
    private val categoriesAmount = 4
    private val mugsAmountPerCategory = 10
    private var categories : ArrayList<Category> = arrayListOf()
    private var mugs : ArrayList<Mug> = arrayListOf()


    @Before
    override fun before() = runTest {
        super.before()
        launch {
            session = SessionRepository.createSession()
            CategoryRepository.insertCategory(Category())
            for (j: Int in 0 until categoriesAmount){
                val catName = "Category ${j+1}"
                val category = Category(getUuidFromString(catName), catName)
                CategoryRepository.insertCategory(category)
                categories.add(category)
                for (i: Int in 1..mugsAmountPerCategory){
                    val artwork = Artwork(genUuid(), imageURL = "", previewURLs = emptyList())
                    val mug = Mug(genUuid(), "", "Mug ${j*mugsAmountPerCategory + i}", "Description", 7.2f, category, artwork, kotlinx.datetime.Clock.System.now(),
                        Mug.urlHandle("Mug ${j*mugsAmountPerCategory + i}", category.name)
                    )
                    mugs.add(mug)
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
            val allMugsPaginated = MugService.getAllMugsList(MugFilter(currentPage = 0, orderBy = Const.OrderBy.NONE))
            assert(allMugsPaginated.size == MugService.mugsPerPage)
            val allMugsPaginated2 = MugService.getAllMugsList(MugFilter(currentPage = 1, orderBy = Const.OrderBy.NONE ))
            assert(allMugsPaginated2[0].name == "Mug ${MugService.mugsPerPage + 1}")

            val allMugsCategoryFilteredPaginated = MugService.getAllMugsList(MugFilter(currentPage = 0, orderBy = Const.OrderBy.NONE , categories = categories.subList(0,3)))
            assert(allMugsCategoryFilteredPaginated.size == MugService.mugsPerPage)
            assert(allMugsCategoryFilteredPaginated[0].category == categories[0])
            assert(allMugsCategoryFilteredPaginated[allMugsCategoryFilteredPaginated.size-1].category == categories[2]) // 25th mug is 3rd category

            val allMugsCategoryFilteredPaginated2 = MugService.getAllMugsList(MugFilter(currentPage = 1, orderBy = Const.OrderBy.NONE, categories = categories.subList(0,3)))
            assert(allMugsCategoryFilteredPaginated2.size == 5) //30 mugs of these 3 categories, 2nd page has 5 mugs left
            assert(allMugsCategoryFilteredPaginated2[0].category == categories[2])

            MugRepository.updateMug(mugs[20].copy(views = 5))
            MugRepository.updateMug(mugs[30].copy(views = 2))
            val allMugsViewsOrderedByViewsPaginated = MugService.getAllMugsList(MugFilter(currentPage = 0, orderBy = Const.OrderBy.VIEWS))
            assert(allMugsViewsOrderedByViewsPaginated[0].id == mugs[20].id)
            assert(allMugsViewsOrderedByViewsPaginated[1].id == mugs[30].id)

            MugRepository.updateMug(mugs[10].copy(dateCreated = kotlinx.datetime.Clock.System.now()))
            val allMugsViewsOrderedPaginated = MugService.getAllMugsList(MugFilter(currentPage = 0, orderBy = Const.OrderBy.MOST_RECENT))
            assert(allMugsViewsOrderedPaginated[0].id == mugs[10].id)

            MugRepository.updateMug(mugs[10].copy(name = "Test Name"))
            val searchList = MugService.getAllMugsList(MugFilter(currentPage = 0, orderBy = Const.OrderBy.MOST_RECENT, searchString = "Test"))
            assert(searchList[0].id == mugs[10].id)
        }
    }

    @Test
    /**
     * Checks that the user is registered, then logged into the session and that
     * all the correct data has been saved to the database
     */
    fun getCategoryTest() = runTest {
        LOG.delimit("Category Tests")
        launch {
            val defaultCategory = CategoryRepository.getCategoryById("0")
            assert(defaultCategory?.name == Const.mugCategoryDefault)
            val category1 = CategoryService.getCategoryByName(categories[0].name)
            assert(category1?.id == categories[0].id)
        }
    }

}