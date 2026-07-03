package com.taoufikcode.chat.data.repository

import com.taoufikcode.chat.data.mappers.toDomain
import com.taoufikcode.chat.data.services.ChatRemoteDataSource
import com.taoufikcode.chat.domain.models.ChatParticipant
import com.taoufikcode.chat.domain.repository.ProfileRepository
import com.taoufikcode.core.domain.auth.SessionStorage
import com.taoufikcode.core.domain.util.DataError
import com.taoufikcode.core.domain.util.EmptyResult
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


    override suspend fun uploadProfilePicture(
        imageBytes: ByteArray,
        mimeType: String
    ): EmptyResult<DataError.Remote> {
        val result = chatRemoteDataSource.getProfilePictureUploadUrl(mimeType).map { it.toDomain() }

        if (result is Result.Failure) {
            return result
        }

        val uploadUrls = (result as Result.Success).data
        val uploadResult = chatRemoteDataSource.uploadProfilePicture(
            uploadUrl = uploadUrls.uploadUrl,
            imageBytes = imageBytes,
            headers = uploadUrls.headers
        )

        if (uploadResult is Result.Failure) {
            return uploadResult
        }

        return chatRemoteDataSource
            .confirmProfilePictureUpload(uploadUrls.publicUrl)
            .onSuccess {
                val currentAuthInfo = sessionStorage.observeAuthInfo().first()
                sessionStorage.set(
                    currentAuthInfo?.copy(
                        user = currentAuthInfo.user.copy(
                            profilePictureUrl = uploadUrls.publicUrl
                        )
                    )
                )
            }
    }

    override suspend fun deleteProfilePicture(): EmptyResult<DataError.Remote> {
        return chatRemoteDataSource
            .deleteProfilePicture()
            .onSuccess {
                val authInfo = sessionStorage.observeAuthInfo().first()
                sessionStorage.set(
                    authInfo?.copy(
                        user = authInfo.user.copy(
                            profilePictureUrl = null
                        )
                    )
                )
            }
    }


}