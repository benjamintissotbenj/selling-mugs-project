package com.benjtissot.sellingmugs.repositories

import com.benjtissot.sellingmugs.Const
import com.benjtissot.sellingmugs.entities.Click
import com.benjtissot.sellingmugs.genUuid
import database
import org.litote.kmongo.upsert

val clickCollection = database.getCollection<Click>()

class ClickRepository {
    companion object {

        /**
         * Creates a [Click] in the database
         * @return the created [Click]
         */
        suspend fun createClick(clickType: Const.ClickType) : Click {
            val newClick = Click(genUuid(), clickType)
            clickCollection.insertOne(newClick)
            return newClick
        }

        /**
         * @param click the [Click] to be updated (inserted if not existent)
         */
        suspend fun updateClick(click: Click) {
            clickCollection.updateOneById(click.id, click, upsert())
        }
    }


}