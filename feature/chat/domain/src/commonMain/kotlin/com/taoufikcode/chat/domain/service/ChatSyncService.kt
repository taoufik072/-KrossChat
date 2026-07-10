package com.taoufikcode.chat.domain.service

import com.taoufikcode.chat.domain.models.Chat
import com.taoufikcode.core.domain.util.DataError
import com.taoufikcode.core.domain.util.EmptyResult

interface ChatSyncService {
    suspend fun refreshChatById(chatId: String): EmptyResult<DataError.Remote>
    suspend fun cacheChat(chat: Chat)
}