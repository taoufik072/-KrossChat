package com.taoufikcode.chat.domain.repository

import com.taoufikcode.chat.domain.models.ChatParticipant
import com.taoufikcode.core.domain.util.DataError
import com.taoufikcode.core.domain.util.Result


interface ProfileRepository {
    suspend fun fetchCurrentUser(): Result<ChatParticipant, DataError>
}