package com.benjtissot.sellingmugs.services

import com.benjtissot.sellingmugs.controllers.mugCollection
import com.benjtissot.sellingmugs.entities.local.Artwork
import com.benjtissot.sellingmugs.entities.local.Mug
import com.benjtissot.sellingmugs.entities.local.UserCustomMugList
import com.benjtissot.sellingmugs.repositories.MugRepository
import org.litote.kmongo.eq

class MugService {
    companion object {

        suspend fun getMugList() : List<Mug> {
            return mugCollection.find().toList().filter {mug -> mug.artwork.public} // only get the publicly available mugs
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
            MugService.getMugByArtwork(artwork)?.copy(
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