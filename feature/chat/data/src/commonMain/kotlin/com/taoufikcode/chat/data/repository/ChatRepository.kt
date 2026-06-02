package com.taoufikcode.chat.data.repository

import com.taoufikcode.chat.data.dto.ChatDto
import com.taoufikcode.chat.data.dto.ChatParticipantDto
import com.taoufikcode.chat.data.dto.CreateChatDto
import com.taoufikcode.chat.data.mappers.toDomain
import com.taoufikcode.chat.domain.ChatService
import com.taoufikcode.chat.domain.models.Chat
import com.taoufikcode.chat.domain.models.ChatParticipant
import com.taoufikcode.core.data.network.get
import com.taoufikcode.core.data.network.post
import com.taoufikcode.core.domain.util.DataError
import com.taoufikcode.core.domain.util.Result
import com.taoufikcode.core.domain.util.map
import io.ktor.client.HttpClient

class ChatRepository(
    private val httpClient: HttpClient
) : ChatService {

    override suspend fun searchParticipant(query: String): Result<ChatParticipant, DataError.Remote> {
        return httpClient.get<ChatParticipantDto>(
            route = "/participants",
            queryParams = mapOf(
                "query" to query
            )
        ).map { it.toDomain() }
    }
    override suspend fun createChat(otherUserIds: List<String>): Result<Chat, DataError.Remote> {
        return httpClient.post<CreateChatDto, ChatDto>(
            route = "/chat",
            body = CreateChatDto(
                otherUserIds = otherUserIds
            )
        ).map { it.toDomain() }
    }
}