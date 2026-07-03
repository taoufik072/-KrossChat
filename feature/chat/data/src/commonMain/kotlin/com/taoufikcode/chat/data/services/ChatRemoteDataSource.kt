package com.taoufikcode.chat.data.services


import com.taoufikcode.chat.data.dto.ConfirmProfilePictureDto
import com.taoufikcode.chat.data.di.response.ProfilePictureUploadUrlsResponse
import com.taoufikcode.chat.data.dto.ChatDto
import com.taoufikcode.chat.data.dto.ChatMessageDto
import com.taoufikcode.chat.data.dto.ChatParticipantDto
import com.taoufikcode.chat.data.dto.CreateChatDto
import com.taoufikcode.chat.data.dto.ParticipantsDto
import com.taoufikcode.core.data.network.delete
import com.taoufikcode.core.data.network.get
import com.taoufikcode.core.data.network.post
import com.taoufikcode.core.data.network.safeCall
import com.taoufikcode.core.domain.util.DataError
import com.taoufikcode.core.domain.util.EmptyResult
import com.taoufikcode.core.domain.util.Result
import com.taoufikcode.core.domain.util.asEmptyResult
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.request.url

class ChatRemoteDataSource(
    private val httpClient: HttpClient
) {
    suspend fun searchParticipant(query: String): Result<ChatParticipantDto, DataError.Remote> {
        return httpClient.get<ChatParticipantDto>(
            route = "/participants",
            queryParams = mapOf(
                "query" to query
            )
        )
    }

    suspend fun getCurrentUserInfo(): Result<ChatParticipantDto, DataError.Remote> {
        return httpClient.get<ChatParticipantDto>(
            route = "/participants"
        )
    }
    suspend fun createChat(otherUserIds: List<String>): Result<ChatDto, DataError.Remote> {
        return httpClient.post<CreateChatDto, ChatDto>(
            route = "/chat",
            body = CreateChatDto(
                otherUserIds = otherUserIds
            )
        )
    }

    suspend fun getChats(): Result<List<ChatDto>, DataError.Remote> {
        return httpClient.get<List<ChatDto>>(route = "/chat")
    }

    suspend fun getChatById(chatId: String): Result<ChatDto, DataError.Remote> {
        return httpClient.get<ChatDto>(
            route = "/chat/$chatId"
        )
    }

    suspend fun leaveChat(chatId: String): EmptyResult<DataError.Remote> {
        return httpClient.delete<Unit>(
            route = "/chat/$chatId/leave"
        ).asEmptyResult()
    }

    suspend fun addParticipantsToChat(
        chatId: String,
        userIds: List<String>
    ): Result<ChatDto, DataError.Remote> {
        return httpClient.post<ParticipantsDto, ChatDto>(
            route = "/chat/$chatId/add",
            body = ParticipantsDto(
                userIds = userIds
            )
        )
    }

    suspend fun fetchMessages(
        chatId: String,
        before: String?
    ): Result<List<ChatMessageDto>, DataError.Remote> {
        return httpClient.get<List<ChatMessageDto>>(
            route = "/chat/$chatId/messages",
            queryParams = buildMap {
                this["pageSize"] = PAGE_SIZE
                if (before != null) {
                    this["before"] = before
                }
            }
        )
    }
     suspend fun deleteMessage(messageId: String): EmptyResult<DataError.Remote> {
        return httpClient.delete(
            route = "/messages/$messageId"
        )
    }

     suspend fun getProfilePictureUploadUrl(mimeType: String): Result<ProfilePictureUploadUrlsResponse, DataError.Remote> {
        return httpClient.post<Unit, ProfilePictureUploadUrlsResponse>(
            route = "/participants/profile-picture-upload",
            queryParams = mapOf(
                "mimeType" to mimeType
            ),
            body = Unit
        )
    }

     suspend fun uploadProfilePicture(
        uploadUrl: String,
        imageBytes: ByteArray,
        headers: Map<String, String>
    ): EmptyResult<DataError.Remote> {
        return safeCall {
            httpClient.put {
                url(uploadUrl)
                headers.forEach { (key, value) ->
                    header(key, value)
                }
                setBody(imageBytes)
            }
        }
    }

     suspend fun confirmProfilePictureUpload(publicUrl: String): EmptyResult<DataError.Remote> {
        return httpClient.post<ConfirmProfilePictureDto, Unit>(
            route = "/participants/confirm-profile-picture",
            body = ConfirmProfilePictureDto(publicUrl)
        )
    }

     suspend fun deleteProfilePicture(): EmptyResult<DataError.Remote> {
        return httpClient.delete(
            route = "/participants/profile-picture"
        )
    }
    companion object {
        const val PAGE_SIZE = 20
    }
}
