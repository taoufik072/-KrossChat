package com.taoufikcode.chat.presentation.mappers

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.taoufikcode.chat.domain.models.ChatMessageDeliveryStatus
import com.taoufikcode.chat.domain.models.MessageWithSender
import com.taoufikcode.chat.presentation.localParticipant
import com.taoufikcode.chat.presentation.model.MessageUi
import com.taoufikcode.chat.presentation.otherParticipant
import com.taoufikcode.chat.presentation.testLocalUser
import com.taoufikcode.chat.presentation.testMessage
import kotlin.test.Test
import kotlin.time.Instant

class ChatMessageMappersTest {

    // 10:00/11:00 UTC keeps both messages on the same local date in every
    // timezone (UTC-12..UTC+14); the third message is 48h earlier so it always
    // lands on a different local date.
    private val newestInstant = Instant.parse("2025-06-12T11:00:00Z")
    private val middleInstant = Instant.parse("2025-06-12T10:00:00Z")
    private val olderDayInstant = Instant.parse("2025-06-10T10:30:00Z")

    private fun withSender(
        id: String,
        senderIsLocal: Boolean,
        createdAt: Instant
    ) = MessageWithSender(
        message = testMessage(
            id = id,
            senderId = if (senderIsLocal) localParticipant.userId else otherParticipant.userId,
            createdAt = createdAt
        ),
        sender = if (senderIsLocal) localParticipant else otherParticipant,
        deliveryStatus = ChatMessageDeliveryStatus.SENT
    )

    @Test
    fun `toUi maps local user message to CurrentUserMessage`() {
        val messageUi = withSender(id = "m1", senderIsLocal = true, createdAt = newestInstant)
            .toUi(localUserId = testLocalUser.id)

        val currentUserMessage = messageUi as MessageUi.CurrentUserMessage
        assertThat(currentUserMessage.id).isEqualTo("m1")
        assertThat(currentUserMessage.content).isEqualTo("Message m1")
        assertThat(currentUserMessage.deliveryStatus).isEqualTo(ChatMessageDeliveryStatus.SENT)
    }

    @Test
    fun `toUi maps other user message to OtherUserMessage with sender`() {
        val messageUi = withSender(id = "m2", senderIsLocal = false, createdAt = newestInstant)
            .toUi(localUserId = testLocalUser.id)

        val otherUserMessage = messageUi as MessageUi.OtherUserMessage
        assertThat(otherUserMessage.id).isEqualTo("m2")
        assertThat(otherUserMessage.sender.username).isEqualTo(otherParticipant.username)
    }

    @Test
    fun `toUiList sorts newest first and inserts a date separator per day`() {
        val messages = listOf(
            withSender(id = "old", senderIsLocal = false, createdAt = olderDayInstant),
            withSender(id = "newest", senderIsLocal = true, createdAt = newestInstant),
            withSender(id = "middle", senderIsLocal = false, createdAt = middleInstant)
        )

        val uiList = messages.toUiList(localUserId = testLocalUser.id)

        // [newest, middle, separator(day1), old, separator(day2)]
        assertThat(uiList).hasSize(5)
        assertThat(uiList[0].id).isEqualTo("newest")
        assertThat(uiList[1].id).isEqualTo("middle")
        assertThat(uiList[2]).isInstanceOf(MessageUi.DateSeparator::class)
        assertThat(uiList[3].id).isEqualTo("old")
        assertThat(uiList[4]).isInstanceOf(MessageUi.DateSeparator::class)
    }

    @Test
    fun `toUiList branches message type on the sender`() {
        val messages = listOf(
            withSender(id = "mine", senderIsLocal = true, createdAt = newestInstant),
            withSender(id = "theirs", senderIsLocal = false, createdAt = middleInstant)
        )

        val uiList = messages.toUiList(localUserId = testLocalUser.id)

        assertThat(uiList[0]).isInstanceOf(MessageUi.CurrentUserMessage::class)
        assertThat(uiList[1]).isInstanceOf(MessageUi.OtherUserMessage::class)
    }

    @Test
    fun `toUiList of empty input is empty`() {
        assertThat(emptyList<MessageWithSender>().toUiList(testLocalUser.id)).hasSize(0)
    }
}
