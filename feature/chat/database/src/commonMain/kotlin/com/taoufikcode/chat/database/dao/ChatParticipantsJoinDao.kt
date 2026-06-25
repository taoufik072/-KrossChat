package com.taoufikcode.chat.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.taoufikcode.chat.database.entities.ActiveParticipantRef
import com.taoufikcode.chat.database.entities.ChatParticipantJoin
import com.taoufikcode.chat.database.entities.ParticipantEntity

@Dao
interface ChatParticipantsJoinDao {

    @Upsert
    suspend fun upsertCrossRefs(crossRefs: List<ChatParticipantJoin>)

    @Query("SELECT chatId, userId FROM chatparticipantjoin WHERE isActive = 1")
    suspend fun getActiveParticipantRefs(): List<ActiveParticipantRef>

    @Query("SELECT userId FROM chatparticipantjoin WHERE chatId = :chatId AND isActive = 1")
    suspend fun getActiveParticipantUserIds(chatId: String): List<String>

    @Query("SELECT userId FROM chatparticipantjoin WHERE chatId = :chatId")
    suspend fun getActiveParticipantIdsByChat(chatId: String): List<String>

    @Query("SELECT userId FROM chatparticipantjoin WHERE chatId = :chatId")
    suspend fun getAllParticipantIdsByChat(chatId: String): List<String>

    @Query("""
        UPDATE chatparticipantjoin
        SET isActive = 0
        WHERE chatId = :chatId AND userId IN (:userIds)
    """)
    suspend fun markParticipantsAsInactive(chatId: String, userIds: List<String>)

    @Query("""
        UPDATE chatparticipantjoin
        SET isActive = 1
        WHERE chatId = :chatId AND userId IN (:userIds)
    """)
    suspend fun reactivateParticipants(chatId: String, userIds: List<String>)

    @Transaction
    suspend fun syncChatParticipants(
        chatId: String,
        participants: List<ParticipantEntity>
    ) {
        if(participants.isEmpty()) {
            return
        }

        val serverParticipantIds = participants.map { it.userId }.toSet()
        val allLocalParticipantIds = getAllParticipantIdsByChat(chatId).toSet()
        val activeLocalParticipantIds = getActiveParticipantIdsByChat(chatId).toSet()
        val inactiveLocalParticipantIds = allLocalParticipantIds - activeLocalParticipantIds

        val participantsToReactivate = serverParticipantIds.intersect(inactiveLocalParticipantIds)
        val participantsToDeactivate = activeLocalParticipantIds - serverParticipantIds

        reactivateParticipants(chatId, participantsToReactivate.toList())
        markParticipantsAsInactive(chatId, participantsToDeactivate.toList())

        val completelyNewParticipantIds = serverParticipantIds - allLocalParticipantIds
        val newCrossRefs = completelyNewParticipantIds.map { userId ->
            ChatParticipantJoin(
                chatId = chatId,
                userId = userId,
                isActive = true
            )
        }
        upsertCrossRefs(newCrossRefs)
    }
}