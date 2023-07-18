package com.benjtissot.sellingmugs.repositories

import com.benjtissot.sellingmugs.entities.stableDiffusion.ImageGeneratedLog
import database
import org.litote.kmongo.eq
import org.litote.kmongo.upsert

val imageGeneratedLogCollection = database.getCollection<ImageGeneratedLog>()

class StableDiffusionRepository {
    companion object {


        /**
         * Gets all logs
         */
        suspend fun getImageGeneratedLogList() : List<ImageGeneratedLog> {
            return imageGeneratedLogCollection.find().toList()
        }

        /**
         * @param imageGeneratedLog the [ImageGeneratedLog] to be inserted
         */
        suspend fun insertImageGeneratedLog(imageGeneratedLog: ImageGeneratedLog) {
            imageGeneratedLogCollection.insertOne(imageGeneratedLog)
        }

        /**
         * @param id the [ImageGeneratedLog.id] to be retrieved
         */
        suspend fun getImageGeneratedLogById(id: String): ImageGeneratedLog? {
            return imageGeneratedLogCollection.findOne(ImageGeneratedLog::id eq id)
        }

        /**
         * @param imageGeneratedLog the [ImageGeneratedLog] to be updated (inserted if not existent)
         */
        suspend fun updateImageGeneratedLog(imageGeneratedLog: ImageGeneratedLog) : ImageGeneratedLog {
            imageGeneratedLogCollection.updateOneById(imageGeneratedLog.id, imageGeneratedLog, upsert())
            return imageGeneratedLog
        }


    }
}