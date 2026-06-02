package com.taoufikcode.chat.domain


import com.taoufikcode.chat.domain.models.ChatParticipant
import com.taoufikcode.core.domain.util.DataError
import com.taoufikcode.core.domain.util.Result

interface ChatParticipantService {
    suspend fun searchParticipant(
        query: String
    ): Result<ChatParticipant, DataError.Remote>
}