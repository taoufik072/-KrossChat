package com.taoufikcode.chat.data.remote


import com.taoufikcode.chat.data.dto.ChatDto
import com.taoufikcode.chat.data.dto.ChatParticipantDto
import com.taoufikcode.chat.data.dto.CreateChatDto
import com.taoufikcode.chat.data.dto.ParticipantsDto
import com.taoufikcode.chat.domain.models.Chat
import com.taoufikcode.core.data.network.delete
import com.taoufikcode.core.data.network.get
import com.taoufikcode.core.data.network.post
import com.taoufikcode.core.domain.util.DataError
import com.taoufikcode.core.domain.util.EmptyResult
import com.taoufikcode.core.domain.util.Result
import com.taoufikcode.core.domain.util.asEmptyResult
import io.ktor.client.HttpClient
import io.ktor.client.request.post

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
}