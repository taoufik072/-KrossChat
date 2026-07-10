package com.taoufikcode.chat.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.taoufikcode.chat.database.entities.MessageEntity
import com.taoufikcode.chat.database.entities.MessageWithSenderEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Dao
interface MessageDao {

    @Upsert
    suspend fun upsertMessage(message: MessageEntity)

    @Upsert
    suspend fun upsertMessages(messages: List<MessageEntity>)

    @Query("DELETE FROM messageentity WHERE messageId = :messageId")
    suspend fun deleteMessageById(messageId: String)

    @Query("DELETE FROM messageentity WHERE messageId IN (:messageIds)")
    suspend fun deleteMessagesById(messageIds: List<String>)

    @Query("SELECT * FROM messageentity WHERE chatId = :chatId ORDER BY timestamp DESC")
    fun getMessagesByChatId(chatId: String): Flow<List<MessageWithSenderEntity>>

    @Query("""
        SELECT *
        FROM messageentity
        WHERE chatId = :chatId
        ORDER BY timestamp DESC
        LIMIT :limit
    """)
    fun getMessagesByChatIdLimited(chatId: String, limit: Int): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messageentity WHERE messageId = :messageId")
    suspend fun getMessageById(messageId: String): MessageEntity?

    @Query("""
        UPDATE messageentity
        SET deliveryStatus = :status, deliveryStatusTimestamp = :timestamp
        WHERE messageId = :messageId
    """)
    suspend fun updateDeliveryStatus(messageId: String, status: String, timestamp: Long)

    @Query("""
        SELECT m.chatId AS chatId, COUNT(*) AS unreadCount
        FROM messageentity m
        LEFT JOIN chatreadstateentity r ON r.chatId = m.chatId
        WHERE m.timestamp > COALESCE(r.lastReadAt, 0) AND m.senderId != :currentUserId
        GROUP BY m.chatId
    """)
    fun observeUnreadCounts(currentUserId: String): Flow<List<ChatUnreadCount>>

    @Transaction
    suspend fun upsertMessagesAndSyncIfNecessary(
        chatId: String,
        serverMessages: List<MessageEntity>,
        pageSize: Int,
        shouldSync: Boolean = false
    ) {
        val localMessages = getMessagesByChatIdLimited(
            chatId = chatId,
            limit = pageSize
        ).first()

        upsertMessages(serverMessages)

        if(!shouldSync) {
            return
        }

        val serverIds = serverMessages.map { it.messageId }.toSet()

        val messagesToDelete = localMessages.filter { localMessage ->
            val missingOnServer = localMessage.messageId !in serverIds
            val isSent = localMessage.deliveryStatus == "SENT"

            missingOnServer && isSent
        }

        val messageIds = messagesToDelete.map { it.messageId }
        deleteMessagesById(messageIds)
    }
}