package com.taoufikcode.chat.data.network

import com.taoufikcode.chat.data.dto.websocket.IncomingWebSocketDto
import com.taoufikcode.chat.data.dto.websocket.IncomingWebSocketType
import com.taoufikcode.chat.data.dto.websocket.WebSocketMessageDto
import com.taoufikcode.chat.data.mappers.toEntity
import com.taoufikcode.chat.database.KrossChatDatabase
import com.taoufikcode.chat.domain.ChatRepository
import com.taoufikcode.core.domain.auth.SessionStorage
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.Json

class IncomingMessageHandler(
    private val json: Json,
    private val chatLocalDataBase: KrossChatDatabase,
    private val sessionStorage: SessionStorage,
    private val chatRepository: ChatRepository,
) {

    fun parse(message: WebSocketMessageDto): IncomingWebSocketDto? {
        return when (message.type) {
            IncomingWebSocketType.NEW_MESSAGE.name -> {
                json.decodeFromString<IncomingWebSocketDto.NewMessageDto>(message.payload)
            }

            IncomingWebSocketType.MESSAGE_DELETED.name -> {
                json.decodeFromString<IncomingWebSocketDto.MessageDeletedDto>(message.payload)
            }

            IncomingWebSocketType.PROFILE_PICTURE_UPDATED.name -> {
                json.decodeFromString<IncomingWebSocketDto.ProfilePictureUpdated>(message.payload)
            }

            IncomingWebSocketType.CHAT_PARTICIPANTS_CHANGED.name -> {
                json.decodeFromString<IncomingWebSocketDto.ChatParticipantsChangedDto>(message.payload)
            }

            else -> null
        }
    }

    suspend fun handle(message: IncomingWebSocketDto) {
        when (message) {
            is IncomingWebSocketDto.ChatParticipantsChangedDto -> refreshChat(message)
            is IncomingWebSocketDto.MessageDeletedDto -> deleteMessage(message)
            is IncomingWebSocketDto.NewMessageDto -> handleNewMessage(message)
            is IncomingWebSocketDto.ProfilePictureUpdated -> updateProfilePicture(message)
        }
    }

    private suspend fun refreshChat(message: IncomingWebSocketDto.ChatParticipantsChangedDto) {
        chatRepository.getChatById(message.chatId)
    }

    private suspend fun deleteMessage(message: IncomingWebSocketDto.MessageDeletedDto) {
        chatLocalDataBase.messageDao.deleteMessageById(message.messageId)
    }

    private suspend fun handleNewMessage(message: IncomingWebSocketDto.NewMessageDto) {
        val chatExists = chatLocalDataBase.chatDao.getChatById(message.chatId) != null
        if (!chatExists) {
            chatRepository.getChatById(message.chatId)
        }

        val entity = message.toEntity()
        chatLocalDataBase.messageDao.upsertMessage(entity)
    }

    private suspend fun updateProfilePicture(message: IncomingWebSocketDto.ProfilePictureUpdated) {
        chatLocalDataBase.participantDao.updateProfilePictureUrl(
            userId = message.userId,
            newUrl = message.newUrl
        )

        val authInfo = sessionStorage.observeAuthInfo().firstOrNull()
        if (authInfo != null) {
            sessionStorage.set(
                info = authInfo.copy(
                    user = authInfo.user.copy(
                        profilePictureUrl = message.newUrl
                    )
                )
            )
        }
    }
}
