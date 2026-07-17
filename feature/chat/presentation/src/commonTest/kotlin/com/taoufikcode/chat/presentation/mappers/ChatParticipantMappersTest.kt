package com.taoufikcode.chat.presentation.mappers

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.taoufikcode.chat.domain.models.ChatParticipant
import com.taoufikcode.core.domain.auth.User
import kotlin.test.Test

class ChatParticipantMappersTest {

    @Test
    fun `ChatParticipant maps to ui with initials`() {
        val participant = ChatParticipant(
            userId = "u1",
            username = "alice",
            profilePictureUrl = "https://cdn.example.com/alice.png"
        )

        val ui = participant.toUi()

        assertThat(ui.id).isEqualTo("u1")
        assertThat(ui.username).isEqualTo("alice")
        assertThat(ui.initials).isEqualTo("AL")
        assertThat(ui.imageUrl).isEqualTo("https://cdn.example.com/alice.png")
    }

    @Test
    fun `User maps to ui with initials from userName`() {
        val user = User(
            id = "u2",
            email = "bob@example.com",
            userName = "bob",
            hasVerifiedEmail = true,
            profilePictureUrl = null
        )

        val ui = user.toUi()

        assertThat(ui.id).isEqualTo("u2")
        assertThat(ui.username).isEqualTo("bob")
        assertThat(ui.initials).isEqualTo("BO")
    }
}
