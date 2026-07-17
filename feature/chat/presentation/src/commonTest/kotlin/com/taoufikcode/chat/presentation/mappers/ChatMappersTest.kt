package com.taoufikcode.chat.presentation.mappers

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.taoufikcode.chat.presentation.localParticipant
import com.taoufikcode.chat.presentation.otherParticipant
import com.taoufikcode.chat.presentation.testChat
import com.taoufikcode.chat.presentation.testLocalUser
import com.taoufikcode.chat.presentation.testMessage
import kotlin.test.Test
import kotlin.time.Instant

class ChatMappersTest {

    private val instant = Instant.parse("2025-06-15T10:30:00Z")

    @Test
    fun `toUi splits current user from other participants`() {
        val chatUi = testChat().toUi(currentUserId = testLocalUser.id)

        assertThat(chatUi.currentUser.id).isEqualTo(localParticipant.userId)
        assertThat(chatUi.otherParticipants.map { it.id })
            .isEqualTo(listOf(otherParticipant.userId))
    }

    @Test
    fun `toUi resolves the last message sender username`() {
        val chat = testChat(
            lastMessage = testMessage(
                id = "m1",
                senderId = otherParticipant.userId,
                createdAt = instant
            )
        )

        val chatUi = chat.toUi(currentUserId = testLocalUser.id)

        assertThat(chatUi.lastMessageSenderUsername).isEqualTo(otherParticipant.username)
        assertThat(chatUi.lastMessage?.id).isEqualTo("m1")
    }

    @Test
    fun `toUi leaves sender username null without a last message`() {
        val chatUi = testChat(lastMessage = null).toUi(currentUserId = testLocalUser.id)

        assertThat(chatUi.lastMessageSenderUsername).isNull()
        assertThat(chatUi.lastMessage).isNull()
    }

    @Test
    fun `toUi carries the unread count`() {
        val chatUi = testChat(unreadCount = 7).toUi(currentUserId = testLocalUser.id)

        assertThat(chatUi.unreadCount).isEqualTo(7)
    }
}
