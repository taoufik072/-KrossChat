package com.taoufikcode.chat.data.mappers

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isGreaterThan
import com.taoufikcode.chat.data.dto.ChatMessageDto
import com.taoufikcode.chat.data.dto.websocket.IncomingWebSocketDto
import com.taoufikcode.chat.data.dto.websocket.OutgoingWebSocketDto
import com.taoufikcode.chat.database.view.LastMessageView
import com.taoufikcode.chat.domain.models.ChatMessage
import com.taoufikcode.chat.domain.models.ChatMessageDeliveryStatus
import com.taoufikcode.chat.domain.models.OutgoingNewMessage
import kotlin.test.Test
import kotlin.time.Instant

class ChatMessageMappersTest {

    private val instant = Instant.parse("2025-06-15T10:30:00Z")

    private val chatMessage = ChatMessage(
        id = "m1",
        chatId = "chat-1",
        content = "Hello",
        createdAt = instant,
        senderId = "u1",
        deliveryStatus = ChatMessageDeliveryStatus.SENT
    )

    @Test
    fun `ChatMessageDto maps to domain with parsed createdAt and SENT status`() {
        val dto = ChatMessageDto(
            id = "m1",
            chatId = "chat-1",
            content = "Hello",
            createdAt = "2025-06-15T10:30:00Z",
            senderId = "u1"
        )

        val message = dto.toDomain()

        assertThat(message).isEqualTo(chatMessage)
    }

    @Test
    fun `LastMessageView maps epoch millis and delivery status to domain`() {
        val view = LastMessageView(
            messageId = "m1",
            chatId = "chat-1",
            senderId = "u1",
            content = "Hello",
            timestamp = instant.toEpochMilliseconds(),
            deliveryStatus = ChatMessageDeliveryStatus.FAILED.name
        )

        val message = view.toDomain()

        assertThat(message).isEqualTo(
            chatMessage.copy(deliveryStatus = ChatMessageDeliveryStatus.FAILED)
        )
    }

    @Test
    fun `ChatMessage round-trips through MessageEntity unchanged`() {
        val entity = chatMessage.toEntity()

        assertThat(entity.toDomain()).isEqualTo(chatMessage)
        assertThat(entity.timestamp).isEqualTo(instant.toEpochMilliseconds())
        assertThat(entity.deliveryStatus).isEqualTo("SENT")
    }

    @Test
    fun `ChatMessage maps to LastMessageView`() {
        val view = chatMessage.toLastMessageView()

        assertThat(view).isEqualTo(
            LastMessageView(
                messageId = "m1",
                chatId = "chat-1",
                senderId = "u1",
                content = "Hello",
                timestamp = instant.toEpochMilliseconds(),
                deliveryStatus = "SENT"
            )
        )
    }

    @Test
    fun `ChatMessage maps to outgoing websocket NewMessage`() {
        val newMessage = chatMessage.toNewMessage()

        assertThat(newMessage).isEqualTo(
            OutgoingWebSocketDto.NewMessage(
                chatId = "chat-1",
                messageId = "m1",
                content = "Hello"
            )
        )
    }

    @Test
    fun `OutgoingNewMessage maps to websocket dto`() {
        val outgoing = OutgoingNewMessage(
            chatId = "chat-1",
            messageId = "m1",
            content = "Hello"
        )

        assertThat(outgoing.toWebSocketDto()).isEqualTo(
            OutgoingWebSocketDto.NewMessage(
                chatId = "chat-1",
                messageId = "m1",
                content = "Hello"
            )
        )
    }

    @Test
    fun `incoming websocket NewMessageDto maps to entity with parsed timestamp and SENT status`() {
        val dto = IncomingWebSocketDto.NewMessageDto(
            id = "m1",
            chatId = "chat-1",
            content = "Hello",
            senderId = "u1",
            createdAt = "2025-06-15T10:30:00Z"
        )

        val entity = dto.toEntity()

        assertThat(entity.messageId).isEqualTo("m1")
        assertThat(entity.timestamp).isEqualTo(instant.toEpochMilliseconds())
        assertThat(entity.deliveryStatus).isEqualTo(ChatMessageDeliveryStatus.SENT.name)
    }

    @Test
    fun `outgoing NewMessage maps to entity with given sender and status`() {
        val newMessage = OutgoingWebSocketDto.NewMessage(
            chatId = "chat-1",
            messageId = "m1",
            content = "Hello"
        )

        val entity = newMessage.toEntity(
            senderId = "u1",
            deliveryStatus = ChatMessageDeliveryStatus.SENDING
        )

        assertThat(entity.messageId).isEqualTo("m1")
        assertThat(entity.chatId).isEqualTo("chat-1")
        assertThat(entity.senderId).isEqualTo("u1")
        assertThat(entity.content).isEqualTo("Hello")
        assertThat(entity.deliveryStatus).isEqualTo("SENDING")
        // Timestamp is stamped with Clock.System.now(); only sanity-check it.
        assertThat(entity.timestamp).isGreaterThan(0L)
    }
}
