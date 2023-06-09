package com.benjtissot.sellingmugs.services

import com.benjtissot.sellingmugs.controllers.mugCollection
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

        suspend fun deleteMug(id: String){
            mugCollection.deleteOne(Mug::id eq id) //type safe
        }
    }
}