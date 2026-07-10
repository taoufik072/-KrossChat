package com.taoufikcode.chat.data.repository

import com.taoufikcode.chat.data.mappers.toDomain
import com.taoufikcode.chat.data.mappers.toEntity
import com.taoufikcode.chat.data.mappers.toLastMessageView
import com.taoufikcode.chat.data.services.ChatRemoteDataSource
import com.taoufikcode.chat.data.services.ChatSyncData
import com.taoufikcode.chat.database.KrossChatDatabase
import com.taoufikcode.chat.database.entities.ChatReadStateEntity
import com.taoufikcode.chat.database.entities.ChatWithParticipantsEntity
import com.taoufikcode.chat.domain.models.Chat
import com.taoufikcode.chat.domain.models.ChatInfo
import com.taoufikcode.chat.domain.models.ChatParticipant
import com.taoufikcode.chat.domain.repository.ChatRepository
import com.taoufikcode.core.domain.auth.SessionStorage
import com.taoufikcode.core.domain.util.DataError
import com.taoufikcode.core.domain.util.EmptyResult
import com.taoufikcode.core.domain.util.Result
import com.taoufikcode.core.domain.util.map
import com.taoufikcode.core.domain.util.onSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlin.time.Clock

class ChatRepositoryImpl(
    private val chatRemoteDataSource: ChatRemoteDataSource,
    private val chatSyncDataSource: ChatSyncData,
    private val chatLocalDataBase: KrossChatDatabase,
    private val sessionStorage: SessionStorage,
) : ChatRepository {

    override suspend fun searchParticipant(query: String): Result<ChatParticipant, DataError.Remote> {
        return chatRemoteDataSource.searchParticipant(query).map { it.toDomain() }
    }

    override suspend fun createChat(otherUserIds: List<String>): Result<Chat, DataError.Remote> {
        return chatRemoteDataSource.createChat(otherUserIds).map { it.toDomain() }
            .onSuccess { chat -> cacheChat(chat) }
    }


    override suspend fun getChats(): Result<List<Chat>, DataError.Remote> {
        return chatRemoteDataSource.getChats().map { chatDto ->
            chatDto.map { it.toDomain() }
        }
    }

    override fun observeChats(): Flow<List<Chat>> {
        val currentUserId = sessionStorage.observeAuthInfo().map { it?.user?.id }

        return combine(
            chatLocalDataBase.chatDao.getChatsWithParticipants(),
            currentUserId
        ) { chats, userId -> chats to userId }
            .flatMapLatest { (allChatsWithParticipants, userId) ->
                val unreadCounts = userId
                    ?.let { chatLocalDataBase.messageDao.observeUnreadCounts(it) }
                    ?: flowOf(emptyList())

                unreadCounts.map { counts ->
                    val unreadCountByChat = counts.associate { it.chatId to it.unreadCount }
                    val activeParticipantIdsByChat = chatLocalDataBase
                        .chatParticipantsJoinDao
                        .getActiveParticipantRefs()
                        .groupBy({ it.chatId }, { it.userId })

                    allChatsWithParticipants.map { chatWithParticipants ->
                        chatWithParticipants.copy(
                            participants = chatWithParticipants.participants.filter {
                                it.userId in activeParticipantIdsByChat[chatWithParticipants.chat.chatId].orEmpty()
                            }
                        ).toDomain().copy(
                            unreadCount = unreadCountByChat[chatWithParticipants.chat.chatId] ?: 0
                        )
                    }
                }
            }
    }

    override fun observeChatById(chatId: String): Flow<ChatInfo> {
        return chatLocalDataBase.chatDao.getChatInfoById(chatId)
            .filterNotNull()
            .map { chatInfo ->
                val activeParticipantIds = chatLocalDataBase
                    .chatParticipantsJoinDao
                    .getActiveParticipantUserIds(chatId)

                chatInfo.copy(
                    participants = chatInfo.participants.filter { it.userId in activeParticipantIds }
                ).toDomain()
            }
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

                // Chats synced for the first time on this device/session (e.g. after
                // login) start out with no local read-state row. Without a baseline,
                // COALESCE(lastReadAt, 0) would treat their entire history as unread.
                // Seed "read up to now" only where no row exists yet, so pre-existing
                // history stays read and only messages arriving after this point count.
                val now = Clock.System.now().toEpochMilliseconds()
                chatLocalDataBase.chatReadStateDao.insertReadStatesIfAbsent(
                    chats.map { chat -> ChatReadStateEntity(chatId = chat.id, lastReadAt = now) }
                )
            }
    }

    override suspend fun getChatById(chatId: String): EmptyResult<DataError.Remote> {
        return chatSyncDataSource.refreshChatById(chatId)

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
            .onSuccess { chat -> cacheChat(chat) }
    }

    override suspend fun deleteAllChats() {
        chatLocalDataBase.chatDao.deleteAllChats()
    }

    override suspend fun markChatAsRead(chatId: String) {
        chatLocalDataBase.chatReadStateDao.upsertReadState(
            ChatReadStateEntity(
                chatId = chatId,
                lastReadAt = Clock.System.now().toEpochMilliseconds()
            )
        )
    }

    private suspend fun cacheChat(chat: Chat) {
        chatLocalDataBase.chatDao.upsertChatWithParticipantsAndCrossRefs(
            chat = chat.toEntity(),
            participants = chat.participants.map { it.toEntity() },
            participantDao = chatLocalDataBase.participantDao,
            crossRefDao = chatLocalDataBase.chatParticipantsJoinDao
        )
    }

}