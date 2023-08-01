package com.benjtissot.sellingmugs.services

import com.benjtissot.sellingmugs.Const
import com.benjtissot.sellingmugs.controllers.mugCollection
import com.benjtissot.sellingmugs.entities.local.*
import com.benjtissot.sellingmugs.repositories.MugRepository
import org.bson.conversions.Bson
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineFindPublisher

class MugService {
    companion object {

        const val mugsPerPage = 25

        /**
         * Creates the appropriate BSON filter
         * @param mugFilter the [MugFilter] object to analyse
         * @return a [Bson] object used in queries
         */
        private fun createFilterFromMugFilter(mugFilter: MugFilter) : Bson {
            val publicFilter = if (mugFilter.publicOnly){
                Mug::artwork / Artwork::public eq true
            } else {
                EMPTY_BSON
            }
            val categoryFilter = if (mugFilter.categories.isNotEmpty()){
                Mug::category `in` mugFilter.categories
            } else {
                EMPTY_BSON
            }
            val searchFilter = if (mugFilter.searchString.isNotBlank()){
                // Have ignore case filtering for the search string
                or (
                    Mug::name.regex(mugFilter.searchString, "i"),
                    Mug::description.regex(mugFilter.searchString, "i"),
                    (Mug::category / Category::name).regex(mugFilter.searchString, "i")
                )
            } else {
                EMPTY_BSON
            }
            return and (publicFilter, categoryFilter, searchFilter)
        }

        /**
         * Creates the correct find method according to the mugFilter object
         * @param sortBy the [Const.OrderBy] indicating how we should sort the results
         * @return a CoroutineFindPublisher to be chain-called
         */
        private fun CoroutineFindPublisher<Mug>.sortBy(sortBy: Const.OrderBy) : CoroutineFindPublisher<Mug> {
            return when (sortBy) {
                Const.OrderBy.VIEWS -> sort(descending(Mug::views))
                Const.OrderBy.NONE -> this
                Const.OrderBy.MOST_RECENT -> sort(
                    descending(listOf(
                        Mug::dateCreated
                    ))
                )
            }
        }

        /**
         * Creates the correct find method according to the mugFilter object
         * @param currentPage the [Int] indicating the current page if we want to paginate, null otherwise
         * @return a CoroutineFindPublisher to be chain-called
         */
        private fun CoroutineFindPublisher<Mug>.paginate(currentPage: Int?) : CoroutineFindPublisher<Mug> {
            return currentPage?.let {
                skip(it * mugsPerPage).limit(mugsPerPage)
            } ?: this
        }

        /**
         * Counts mugs
         */
        suspend fun getMugCount(mugFilter: MugFilter) : Int {
            val filter = createFilterFromMugFilter(mugFilter)
            return mugCollection.countDocuments(filter).toInt()
        }

        /**
         * Gets all the publicly available mugs
         */
        suspend fun getPublicMugList(mugFilter: MugFilter = MugFilter()) : List<Mug> {
            return getAllMugsList(mugFilter.copy(publicOnly = true)) // only get the publicly available mugs
        }

        /**
         * Gets all the mugs in the database, can be paginated, filtered
         * @param mugFilter a [MugFilter] object that holds all the information to filter out which mugs we want to retrieve,
         * pagination information and ordering information
         */
        suspend fun getAllMugsList(mugFilter : MugFilter = MugFilter()) : List<Mug> {
            val filter = createFilterFromMugFilter(mugFilter)
            return mugCollection.find(filter)
                .sortBy(mugFilter.orderBy)
                .paginate(mugFilter.currentPage)
                .toList()
        }

        /**
         * Increases by one the amount of views a given mug has received (to perform ordering)
         */
        suspend fun increaseMugViews(printifyId: String){
            getMugByPrintifyId(printifyId)?.let{
                MugRepository.updateMug(it.copy(views = it.views + 1))
            }
        }

        suspend fun insertNewMug(mug: Mug){
            MugRepository.updateMug(mug)
        }

        suspend fun getMugByPrintifyId(printifyId: String): Mug? {
            return MugRepository.getMugByPrintifyId(printifyId)
        }

        private suspend fun getMugByArtwork(artwork: Artwork): Mug? {
            return MugRepository.getMugByArtwork(artwork)
        }

        suspend fun deleteMug(id: String){
            mugCollection.deleteOne(Mug::id eq id) //type safe
        }

        suspend fun deleteMugByPrintifyId(printifyId: String){
            mugCollection.deleteOne(Mug::printifyId eq printifyId) //type safe
        }

        suspend fun updateArtworkImage(artwork : Artwork, printifyProductId : String){
            getMugByArtwork(artwork)?.copy(
                artwork = ArtworkService.updateArtwork(
                    artwork.copy(previewURLs = PrintifyService.getProductPreviewImages(printifyProductId))
                )
            )?.let {
                MugRepository.updateMug(it)
            }
        }

        /**
         * Creates a user's custom mug list to retrieve easily
         */
        suspend fun createUserCustomMugList(userId: String) : UserCustomMugList {
            val userCustomMugList = UserCustomMugList(userId, emptyList())
            MugRepository.insertUserCustomMugList(userCustomMugList)
            return userCustomMugList
        }

        /**
         * Get a user's custom mug list
         */
        suspend fun getUserCustomMugList(userId: String) : List<Mug> {
            val customMugList = MugRepository.getUserCustomMugListByUserId(userId) ?: createUserCustomMugList(userId)
            return customMugList.mugIds.mapNotNull { mugId -> MugRepository.getMugById(mugId) }
        }

        /**
         * Insert a new mug in a user's custom mug list
         */
        suspend fun addMugToUserCustomMugList(userId: String, mugId: String){
            MugRepository.addMugToUserCustomMugList(userId, mugId)
        }
    }
}