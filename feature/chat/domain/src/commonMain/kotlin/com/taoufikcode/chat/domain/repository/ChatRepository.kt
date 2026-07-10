package com.taoufikcode.chat.domain.repository


import com.taoufikcode.chat.domain.models.Chat
import com.taoufikcode.chat.domain.models.ChatInfo
import com.taoufikcode.chat.domain.models.ChatParticipant
import com.taoufikcode.core.domain.util.DataError
import com.taoufikcode.core.domain.util.EmptyResult
import com.taoufikcode.core.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun searchParticipant(
        query: String
    ): Result<ChatParticipant, DataError.Remote>

    suspend fun createChat(
        otherUserIds: List<String>
    ): Result<Chat, DataError.Remote>

    suspend fun getChats(): Result<List<Chat>, DataError.Remote>

    fun observeChats(): Flow<List<Chat>>

    fun observeChatById(chatId: String): Flow<ChatInfo>

    fun observeActiveParticipantsByChatId(chatId: String): Flow<List<ChatParticipant>>

    suspend fun fetchChats(): Result<List<Chat>, DataError.Remote>


    suspend fun getChatById(chatId: String): EmptyResult<DataError.Remote>

    suspend fun leaveChat(chatId: String): EmptyResult<DataError.Remote>

    suspend fun addParticipantsToChat(
        chatId: String,
        userIds: List<String>
    ): Result<Chat, DataError.Remote>

    suspend fun deleteAllChats()

    suspend fun markChatAsRead(chatId: String)
}