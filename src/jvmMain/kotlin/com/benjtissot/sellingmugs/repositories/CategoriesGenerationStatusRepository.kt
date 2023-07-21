package com.benjtissot.sellingmugs.repositories

import com.benjtissot.sellingmugs.entities.openAI.GenerateCategoriesStatus
import com.benjtissot.sellingmugs.entities.stableDiffusion.ImageGeneratedLog
import database
import org.litote.kmongo.eq
import org.litote.kmongo.upsert

val generateCategoriesStatusCollection = database.getCollection<GenerateCategoriesStatus>()

class CategoriesGenerationResultRepository {
    companion object {


        /**
         * @param id the [GenerateCategoriesStatus.id] to be retrieved
         */
        suspend fun getGenerateCategoriesStatusById(id: String): GenerateCategoriesStatus? {
            return generateCategoriesStatusCollection.findOne(GenerateCategoriesStatus::id eq id)
        }

        /**
         * @param generateCategoriesStatus the [GenerateCategoriesStatus] to be updated (inserted if not existent)
         */
        suspend fun updateGenerateCategoriesStatus(generateCategoriesStatus: GenerateCategoriesStatus) : GenerateCategoriesStatus {
            generateCategoriesStatusCollection.updateOneById(generateCategoriesStatus.id, generateCategoriesStatus, upsert())
            return generateCategoriesStatus
        }


    }
}