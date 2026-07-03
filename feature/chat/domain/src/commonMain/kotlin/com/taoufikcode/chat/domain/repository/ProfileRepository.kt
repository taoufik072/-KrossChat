package com.taoufikcode.chat.domain.repository

import com.taoufikcode.chat.domain.models.ChatParticipant
import com.taoufikcode.chat.domain.models.ProfilePictureUploadUrls
import com.taoufikcode.core.domain.util.DataError
import com.taoufikcode.core.domain.util.EmptyResult
import com.taoufikcode.core.domain.util.Result


interface ProfileRepository {
    suspend fun fetchCurrentUser(): Result<ChatParticipant, DataError>
    suspend fun uploadProfilePicture(
        imageBytes: ByteArray,
        mimeType: String
    ): EmptyResult<DataError.Remote>

    suspend fun deleteProfilePicture(): EmptyResult<DataError.Remote>

}