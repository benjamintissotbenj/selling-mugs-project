package com.benjtissot.sellingmugs.repositories

import com.benjtissot.sellingmugs.entities.User
import com.benjtissot.sellingmugs.entities.openAI.ChatLog
import database
import org.litote.kmongo.eq
import org.litote.kmongo.upsert

val chatLogCollection = database.getCollection<ChatLog>()

class ChatRepository {
    companion object {


        /**
         * Gets all logs
         */
        suspend fun getChatLogList() : List<ChatLog> {
            return chatLogCollection.find().toList()
        }

        /**
         * @param chatLog the [ChatLog] to be inserted
         */
        suspend fun insertChatLog(chatLog: ChatLog) {
            chatLogCollection.insertOne(chatLog)
        }

        /**
         * @param chatLog the [ChatLog] to be inserted
         */
        suspend fun getChatLogById(id: String): ChatLog? {
            return chatLogCollection.findOne(ChatLog::id eq id)
        }

        /**
         * @param chatLog the [ChatLog] to be updated (inserted if not existent)
         */
        suspend fun updateChatLog(chatLog: ChatLog) : ChatLog {
            chatLogCollection.updateOneById(chatLog.id, chatLog, upsert())
            return chatLog
        }


    }
}