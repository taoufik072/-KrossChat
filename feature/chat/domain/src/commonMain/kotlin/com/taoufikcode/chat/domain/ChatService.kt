package com.taoufikcode.chat.domain


import com.taoufikcode.chat.domain.models.Chat
import com.taoufikcode.chat.domain.models.ChatParticipant
import com.taoufikcode.core.domain.util.DataError
import com.taoufikcode.core.domain.util.Result

interface ChatService {
    suspend fun searchParticipant(
        query: String
    ): Result<ChatParticipant, DataError.Remote>

    suspend fun createChat(
        otherUserIds: List<String>
    ): Result<Chat, DataError.Remote>
}