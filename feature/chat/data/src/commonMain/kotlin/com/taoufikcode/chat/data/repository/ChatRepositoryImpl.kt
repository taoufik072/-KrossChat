package com.taoufikcode.chat.data.repository

import com.taoufikcode.chat.data.mappers.toDomain
import com.taoufikcode.chat.data.mappers.toEntity
import com.taoufikcode.chat.data.mappers.toLastMessageView
import com.taoufikcode.chat.data.remote.ChatRemoteDataSource
import com.taoufikcode.chat.database.KrossChatDatabase
import com.taoufikcode.chat.database.entities.ChatInfoEntity
import com.taoufikcode.chat.database.entities.ChatWithParticipantsEntity
import com.taoufikcode.chat.database.entities.ParticipantEntity
import com.taoufikcode.chat.domain.ChatRepository
import com.taoufikcode.chat.domain.models.Chat
import com.taoufikcode.chat.domain.models.ChatInfo
import com.taoufikcode.chat.domain.models.ChatParticipant
import com.taoufikcode.core.domain.util.DataError
import com.taoufikcode.core.domain.util.EmptyResult
import com.taoufikcode.core.domain.util.Result
import com.taoufikcode.core.domain.util.asEmptyResult
import com.taoufikcode.core.domain.util.map
import com.taoufikcode.core.domain.util.onSuccess
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.supervisorScope

class ChatRepositoryImpl(
    private val chatRemoteDataSource: ChatRemoteDataSource,
    private val chatLocalDataBase: KrossChatDatabase
) : ChatRepository {
    override suspend fun searchParticipant(query: String): Result<ChatParticipant, DataError.Remote> {
        return chatRemoteDataSource.searchParticipant(query).map { it.toDomain() }
    }

    override suspend fun createChat(otherUserIds: List<String>): Result<Chat, DataError.Remote> {
        return chatRemoteDataSource.createChat(otherUserIds).map { it.toDomain() }
            .onSuccess { chat ->
                chatLocalDataBase.chatDao.upsertChatWithParticipantsAndCrossRefs(
                    chat = chat.toEntity(),
                    participants = chat.participants.map { it.toEntity() },
                    participantDao = chatLocalDataBase.participantDao,
                    crossRefDao = chatLocalDataBase.chatParticipantsJoinDao
                )
            }
    }


    override suspend fun getChats(): Result<List<Chat>, DataError.Remote> {
        return chatRemoteDataSource.getChats().map { chatDto ->
            chatDto.map { it.toDomain() }
        }
    }

    override fun observeChats(): Flow<List<Chat>> {
        return chatLocalDataBase.chatDao.getChatsWithParticipants()
            .map { allChatsWithParticipants ->
                supervisorScope {
                    allChatsWithParticipants
                        .map { chatWithParticipants ->
                            async {
                                ChatWithParticipantsEntity(
                                    chat = chatWithParticipants.chat,
                                    participants = chatWithParticipants
                                        .participants
                                        .onlyActive(chatWithParticipants.chat.chatId),
                                    lastMessage = chatWithParticipants.lastMessage
                                )
                            }
                        }
                        .awaitAll()
                        .map { it.toDomain() }
                }
            }
    }

    override fun observeChatById(chatId: String): Flow<ChatInfo> {
        return chatLocalDataBase.chatDao.getChatInfoById(chatId)
            .filterNotNull()
            .map { chatInfo ->
                ChatInfoEntity(
                    chat = chatInfo.chat,
                    participants = chatInfo
                        .participants
                        .onlyActive(chatInfo.chat.chatId),
                    messagesWithSenders = chatInfo.messagesWithSenders
                )
            }
            .map { it.toDomain() }
    }

    override fun observeActiveParticipantsByChatId(chatId: String): Flow<List<ChatParticipant>> {
        return chatLocalDataBase.chatDao.getActiveParticipantsByChatId(chatId)
            .map { participants ->
                participants.map { it.toDomain() }
            }
    }

    override suspend fun fetchChats(): Result<List<Chat>, DataError.Remote> {
        return chatRemoteDataSource.getChats().map { chatDto -> chatDto.map { it.toDomain() } }
            .onSuccess { chats ->
                val chatsWithParticipants = chats.map { chat ->
                    ChatWithParticipantsEntity(
                        chat = chat.toEntity(),
                        participants = chat.participants.map { it.toEntity() },
                        lastMessage = chat.lastMessage?.toLastMessageView()
                    )
                }

                chatLocalDataBase.chatDao.upsertChatsWithParticipantsAndCrossRefs(
                    chats = chatsWithParticipants,
                    participantDao = chatLocalDataBase.participantDao,
                    crossRefDao = chatLocalDataBase.chatParticipantsJoinDao,
                    messageDao = chatLocalDataBase.messageDao
                )
            }
    }

    override suspend fun getChatById(chatId: String): EmptyResult<DataError.Remote> {
        return chatRemoteDataSource.getChatById(chatId).map { it.toDomain() }
            .onSuccess { chat ->
                chatLocalDataBase.chatDao.upsertChatWithParticipantsAndCrossRefs(
                    chat = chat.toEntity(),
                    participants = chat.participants.map { it.toEntity() },
                    participantDao = chatLocalDataBase.participantDao,
                    crossRefDao = chatLocalDataBase.chatParticipantsJoinDao
                )
            }
            .asEmptyResult()
    }

    override suspend fun leaveChat(chatId: String): EmptyResult<DataError.Remote> {
        return chatRemoteDataSource
            .leaveChat(chatId)
            .onSuccess {
                chatLocalDataBase.chatDao.deleteChatById(chatId)
            }
    }

    override suspend fun addParticipantsToChat(
        chatId: String,
        userIds: List<String>
    ): Result<Chat, DataError.Remote> {
        return chatRemoteDataSource
            .addParticipantsToChat(chatId, userIds)
            .map { it.toDomain() }
            .onSuccess { chat ->
                chatLocalDataBase.chatDao.upsertChatWithParticipantsAndCrossRefs(
                    chat = chat.toEntity(),
                    participants = chat.participants.map { it.toEntity() },
                    participantDao = chatLocalDataBase.participantDao,
                    crossRefDao = chatLocalDataBase.chatParticipantsJoinDao
                )
            }
    }

    private suspend fun List<ParticipantEntity>.onlyActive(chatId: String): List<ParticipantEntity> {
        val activeParticipantIds = chatLocalDataBase
            .chatDao
            .getActiveParticipantsByChatId(chatId)
            .first()
            .map { it.userId }

        return this.filter { it.userId in activeParticipantIds }
    }

}