package com.benjtissot.sellingmugs.repositories

import com.benjtissot.sellingmugs.Const
import com.benjtissot.sellingmugs.entities.Click
import com.benjtissot.sellingmugs.entities.ClickData
import com.benjtissot.sellingmugs.entities.Session
import com.benjtissot.sellingmugs.genUuid
import database
import kotlinx.serialization.internal.throwArrayMissingFieldException
import org.litote.kmongo.eq

val clickDataCollection = database.getCollection<ClickData>()

class ClickDataRepository {
    companion object {
        suspend fun createClickData() : ClickData {
            val clickData = ClickData(genUuid().toString(), arrayListOf<Click>())
            clickDataCollection.insertOne(clickData)
            return clickData
        }

        /**
         * Gets a [ClickData] by its [ClickData.id] from the database
         * @param id the id of the [ClickData] to retrieve
         * @return the [ClickData] if found, null otherwise
         */
        suspend fun getClickDataById(id: String) : ClickData? {
            return clickDataCollection.find(ClickData::id eq id).first()
        }

        /**
         * @param clickData the [ClickData] to be updated (inserted if not existent)
         */
        suspend fun updateClickData(clickData: ClickData) {
            clickDataCollection.updateOneById(clickData.id, clickData).also {
                if (!it.wasAcknowledged()) {
                    clickDataCollection.insertOne(clickData)
                }
            }
        }

        suspend fun  addClickById(clickDataId: String, clickType: Const.ClickType){
            val click = ClickRepository.createClick(clickType)
            val clickData = getClickDataById(clickDataId)
            clickData?.also {
                it.clicks.add(click)
                updateClickData(it)
            }
        }
    }


}