package com.taoufikcode.chat.data.services

import com.taoufikcode.chat.data.mappers.toDomain
import com.taoufikcode.chat.data.mappers.toEntity
import com.taoufikcode.chat.database.KrossChatDatabase
import com.taoufikcode.chat.domain.models.Chat
import com.taoufikcode.chat.domain.service.ChatSyncService
import com.taoufikcode.core.domain.util.DataError
import com.taoufikcode.core.domain.util.EmptyResult
import com.taoufikcode.core.domain.util.asEmptyResult
import com.taoufikcode.core.domain.util.map
import com.taoufikcode.core.domain.util.onSuccess

class ChatSyncData(
    private val chatLocalDatabase: KrossChatDatabase,
    private val remoteDataSource: ChatRemoteDataSource
) : ChatSyncService {
    override suspend fun refreshChatById(chatId: String): EmptyResult<DataError.Remote> {
        return remoteDataSource.getChatById(chatId)
            .map { it.toDomain() }
            .onSuccess { chat -> cacheChat(chat) }
            .asEmptyResult()
    }

    override suspend fun cacheChat(chat: Chat) {
        chatLocalDatabase.chatDao.upsertChatWithParticipantsAndCrossRefs(
            chat = chat.toEntity(),
            participants = chat.participants.map { it.toEntity() },
            participantDao = chatLocalDatabase.participantDao,
            crossRefDao = chatLocalDatabase.chatParticipantsJoinDao
        )
    }

}