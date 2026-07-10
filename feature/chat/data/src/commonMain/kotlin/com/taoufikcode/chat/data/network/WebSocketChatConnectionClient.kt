package com.taoufikcode.chat.data.network

import com.taoufikcode.chat.data.dto.websocket.IncomingWebSocketDto
import com.taoufikcode.chat.data.dto.websocket.IncomingWebSocketType
import com.taoufikcode.chat.data.dto.websocket.WebSocketMessageDto
import com.taoufikcode.chat.data.mappers.toDomain
import com.taoufikcode.chat.data.mappers.toEntity
import com.taoufikcode.chat.data.mappers.toNewMessage
import com.taoufikcode.chat.data.services.ChatSyncData
import com.taoufikcode.chat.database.KrossChatDatabase
import com.taoufikcode.chat.domain.models.ChatMessage
import com.taoufikcode.chat.domain.models.ChatMessageDeliveryStatus
import com.taoufikcode.chat.domain.service.ChatConnectionClient
import com.taoufikcode.core.data.database.safeDatabaseUpdate
import com.taoufikcode.core.domain.auth.SessionStorage
import com.taoufikcode.core.domain.util.DataError
import com.taoufikcode.core.domain.util.EmptyResult
import com.taoufikcode.core.domain.util.onFailure
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.shareIn
import kotlinx.serialization.json.Json
import kotlin.time.Clock

class WebSocketChatConnectionClient(
    private val webSocketConnector: KtorWebSocketConnector,
    private val chatLocalDataBase: KrossChatDatabase,
    private val chatSyncData: ChatSyncData,
    private val json: Json,
    private val sessionStorage: SessionStorage,
    applicationScope: CoroutineScope,
) : ChatConnectionClient {

    override val connectionState = webSocketConnector.connectionState

    override val chatMessages: Flow<ChatMessage> = webSocketConnector
        .messages
        .mapNotNull { parse(it) }
        .mapNotNull { handle(it) }
        .shareIn(
            applicationScope,
            SharingStarted.WhileSubscribed(5000)
        )

    override suspend fun sendMessage(message: ChatMessage): EmptyResult<DataError.Connection> {
        val outgoingDto = message.toNewMessage()
        val webSocketMessage = WebSocketMessageDto(
            type = outgoingDto.type.name,
            payload = json.encodeToString(outgoingDto)
        )
        val rawJsonPayload = json.encodeToString(webSocketMessage)

        return webSocketConnector
            .sendMessage(rawJsonPayload)
            .onFailure { error ->
                updateMessageDeliveryStatus(
                    messageId = message.id,
                    status = ChatMessageDeliveryStatus.FAILED
                )
            }
    }

    override suspend fun updateMessageDeliveryStatus(
        messageId: String,
        status: ChatMessageDeliveryStatus
    ): EmptyResult<DataError.Local> {
        return safeDatabaseUpdate {
            chatLocalDataBase.messageDao.updateDeliveryStatus(
                messageId = messageId,
                status = status.name,
                timestamp = Clock.System.now().toEpochMilliseconds()
            )
        }
    }


    suspend fun handle(message: IncomingWebSocketDto): ChatMessage? {
        return when (message) {
            is IncomingWebSocketDto.NewMessageDto -> handleNewMessage(message)
            is IncomingWebSocketDto.ProfilePictureUpdated -> {
                updateProfilePicture(message)
                null
            }

            is IncomingWebSocketDto.ChatParticipantsChangedDto -> {
                chatSyncData.refreshChatById(message.chatId)
                null
            }

            is IncomingWebSocketDto.MessageDeletedDto -> {
                deleteMessage(message.messageId)
                null
            }
        }
    }

    private suspend fun handleNewMessage(message: IncomingWebSocketDto.NewMessageDto): ChatMessage {
        val chatExists = chatLocalDataBase.chatDao.getChatById(message.chatId) != null
        if (!chatExists) {
            chatSyncData.refreshChatById(message.chatId)
        }

        val entity = message.toEntity()
        chatLocalDataBase.messageDao.upsertMessage(entity)
        return entity.toDomain()
    }

    private suspend fun deleteMessage(messageId: String) {
        chatLocalDataBase.messageDao.deleteMessageById(messageId)
    }

    private suspend fun updateProfilePicture(message: IncomingWebSocketDto.ProfilePictureUpdated) {
        chatLocalDataBase.participantDao.updateProfilePictureUrl(
            userId = message.userId,
            newUrl = message.newUrl
        )

        val authInfo = sessionStorage.observeAuthInfo().firstOrNull()
        if (authInfo != null && authInfo.user.id == message.userId) {
            sessionStorage.set(
                info = authInfo.copy(
                    user = authInfo.user.copy(
                        profilePictureUrl = message.newUrl
                    )
                )
            )
        }
    }
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
}