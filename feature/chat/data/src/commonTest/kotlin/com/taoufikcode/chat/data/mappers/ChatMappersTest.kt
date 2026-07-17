package com.taoufikcode.chat.data.mappers

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.taoufikcode.chat.data.dto.ChatDto
import com.taoufikcode.chat.data.dto.ChatMessageDto
import com.taoufikcode.chat.data.dto.ChatParticipantDto
import com.taoufikcode.chat.database.entities.ChatEntity
import com.taoufikcode.chat.database.entities.ChatInfoEntity
import com.taoufikcode.chat.database.entities.ChatWithParticipantsEntity
import com.taoufikcode.chat.database.entities.MessageEntity
import com.taoufikcode.chat.database.entities.MessageWithSenderEntity
import com.taoufikcode.chat.database.entities.ParticipantEntity
import com.taoufikcode.chat.database.view.LastMessageView
import com.taoufikcode.chat.domain.models.ChatMessageDeliveryStatus
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.time.Instant

class ChatMappersTest {

    private val instant = Instant.parse("2025-06-15T10:30:00Z")

    private val participantEntity = ParticipantEntity(
        userId = "u1",
        username = "alice",
        profilePictureUrl = null
    )

    private val messageEntity = MessageEntity(
        messageId = "m1",
        chatId = "chat-1",
        senderId = "u1",
        content = "Hello",
        timestamp = instant.toEpochMilliseconds(),
        deliveryStatus = ChatMessageDeliveryStatus.SENT.name
    )

    @Test
    fun `ChatDto maps to domain and parses ISO-8601 lastActivityAt`() {
        val dto = ChatDto(
            id = "chat-1",
            participants = listOf(
                ChatParticipantDto(userId = "u1", username = "alice", profilePictureUrl = null)
            ),
            lastActivityAt = "2025-06-15T10:30:00Z",
            lastMessage = ChatMessageDto(
                id = "m1",
                chatId = "chat-1",
                content = "Hello",
                createdAt = "2025-06-15T10:30:00Z",
                senderId = "u1"
            )
        )

        val chat = dto.toDomain()

        assertThat(chat.id).isEqualTo("chat-1")
        assertThat(chat.lastActivityAt).isEqualTo(instant)
        assertThat(chat.participants.single().userId).isEqualTo("u1")
        assertThat(chat.lastMessage?.content).isEqualTo("Hello")
    }

    @Test
    fun `ChatDto with malformed lastActivityAt throws`() {
        val dto = ChatDto(
            id = "chat-1",
            participants = emptyList(),
            lastActivityAt = "not-a-date",
            lastMessage = null
        )

        assertFailsWith<IllegalArgumentException> { dto.toDomain() }
    }

    @Test
    fun `ChatWithParticipantsEntity maps epoch millis and last message to domain`() {
        val entity = ChatWithParticipantsEntity(
            chat = ChatEntity(chatId = "chat-1", lastActivityAt = instant.toEpochMilliseconds()),
            participants = listOf(participantEntity),
            lastMessage = LastMessageView(
                messageId = "m1",
                chatId = "chat-1",
                senderId = "u1",
                content = "Hello",
                timestamp = instant.toEpochMilliseconds(),
                deliveryStatus = ChatMessageDeliveryStatus.SENT.name
            )
        )

        val chat = entity.toDomain()

        assertThat(chat.lastActivityAt).isEqualTo(instant)
        assertThat(chat.lastMessage?.id).isEqualTo("m1")
        assertThat(chat.lastMessage?.deliveryStatus).isEqualTo(ChatMessageDeliveryStatus.SENT)
    }

    @Test
    fun `ChatWithParticipantsEntity without last message maps to null`() {
        val entity = ChatWithParticipantsEntity(
            chat = ChatEntity(chatId = "chat-1", lastActivityAt = instant.toEpochMilliseconds()),
            participants = listOf(participantEntity),
            lastMessage = null
        )

        assertThat(entity.toDomain().lastMessage).isNull()
    }

    @Test
    fun `Chat round-trips through ChatEntity keeping id and activity time`() {
        val chat = ChatWithParticipantsEntity(
            chat = ChatEntity(chatId = "chat-1", lastActivityAt = instant.toEpochMilliseconds()),
            participants = listOf(participantEntity),
            lastMessage = null
        ).toDomain()

        val entity = chat.toEntity()

        assertThat(entity).isEqualTo(
            ChatEntity(chatId = "chat-1", lastActivityAt = instant.toEpochMilliseconds())
        )
    }

    @Test
    fun `MessageWithSenderEntity maps delivery status from string`() {
        val entity = MessageWithSenderEntity(
            message = messageEntity.copy(
                deliveryStatus = ChatMessageDeliveryStatus.FAILED.name
            ),
            sender = participantEntity
        )

        val messageWithSender = entity.toDomain()

        assertThat(messageWithSender.deliveryStatus).isEqualTo(ChatMessageDeliveryStatus.FAILED)
        assertThat(messageWithSender.message.senderId).isEqualTo("u1")
        assertThat(messageWithSender.sender.username).isEqualTo("alice")
    }

    @Test
    fun `MessageWithSenderEntity with unknown delivery status throws`() {
        val entity = MessageWithSenderEntity(
            message = messageEntity.copy(deliveryStatus = "NOT_A_STATUS"),
            sender = participantEntity
        )

        assertFailsWith<IllegalArgumentException> { entity.toDomain() }
    }

    @Test
    fun `ChatInfoEntity maps chat with participants and messages`() {
        val entity = ChatInfoEntity(
            chat = ChatEntity(chatId = "chat-1", lastActivityAt = instant.toEpochMilliseconds()),
            participants = listOf(participantEntity),
            messagesWithSenders = listOf(
                MessageWithSenderEntity(message = messageEntity, sender = participantEntity)
            )
        )

        val chatInfo = entity.toDomain()

        assertThat(chatInfo.chat.id).isEqualTo("chat-1")
        assertThat(chatInfo.chat.participants.single().username).isEqualTo("alice")
        assertThat(chatInfo.messages.single().message.id).isEqualTo("m1")
    }
}
