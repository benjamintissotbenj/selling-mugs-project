package com.benjtissot.sellingmugs.services

import com.benjtissot.sellingmugs.controllers.mugCollection
import com.benjtissot.sellingmugs.entities.Artwork
import com.benjtissot.sellingmugs.entities.Mug
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

        suspend fun getMugByArtwork(artwork: Artwork): Mug? {
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
    }
}