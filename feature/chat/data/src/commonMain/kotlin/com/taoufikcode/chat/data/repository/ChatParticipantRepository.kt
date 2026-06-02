package com.taoufikcode.chat.data.repository

import com.taoufikcode.chat.data.dto.ChatParticipantDto
import com.taoufikcode.chat.data.mappers.toDomain
import com.taoufikcode.chat.domain.ChatParticipantService
import com.taoufikcode.chat.domain.models.ChatParticipant
import com.taoufikcode.core.data.network.get
import com.taoufikcode.core.domain.util.DataError
import com.taoufikcode.core.domain.util.Result
import com.taoufikcode.core.domain.util.map
import io.ktor.client.HttpClient

class ChatParticipantRepository(
    private val httpClient: HttpClient
) : ChatParticipantService {

    override suspend fun searchParticipant(query: String): Result<ChatParticipant, DataError.Remote> {
        return httpClient.get<ChatParticipantDto>(
            route = "/participants",
            queryParams = mapOf(
                "query" to query
            )
        ).map { it.toDomain() }
    }
}