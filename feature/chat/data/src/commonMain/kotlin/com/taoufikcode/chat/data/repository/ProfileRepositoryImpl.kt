package com.taoufikcode.chat.data.repository

import com.taoufikcode.chat.data.mappers.toDomain
import com.taoufikcode.chat.data.services.ChatRemoteDataSource
import com.taoufikcode.chat.domain.models.ChatParticipant
import com.taoufikcode.chat.domain.repository.ProfileRepository
import com.taoufikcode.core.domain.auth.SessionStorage
import com.taoufikcode.core.domain.util.DataError
import com.taoufikcode.core.domain.util.Result
import com.taoufikcode.core.domain.util.map
import com.taoufikcode.core.domain.util.onSuccess
import kotlinx.coroutines.flow.first


class ProfileRepositoryImpl(
    private val sessionStorage: SessionStorage,
    private val chatRemoteDataSource: ChatRemoteDataSource,
) : ProfileRepository {

    override suspend fun fetchCurrentUser(): Result<ChatParticipant, DataError> {
        return chatRemoteDataSource
            .getCurrentUserInfo()
            .map { it.toDomain() }
            .onSuccess { participant ->
                val currentAuthInfo = sessionStorage.observeAuthInfo().first()
                sessionStorage.set(
                    currentAuthInfo?.copy(
                        user = currentAuthInfo.user.copy(
                            id = participant.userId,
                            userName = participant.username,
                            profilePictureUrl = participant.profilePictureUrl
                        )
                    )
                )
            }
    }
}