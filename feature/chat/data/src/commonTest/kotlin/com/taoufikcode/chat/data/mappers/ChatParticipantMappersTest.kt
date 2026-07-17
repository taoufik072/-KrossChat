package com.taoufikcode.chat.data.mappers

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.taoufikcode.chat.data.dto.ChatParticipantDto
import com.taoufikcode.chat.database.entities.ParticipantEntity
import com.taoufikcode.chat.domain.models.ChatParticipant
import kotlin.test.Test

class ChatParticipantMappersTest {

    @Test
    fun `ChatParticipantDto maps to domain`() {
        val dto = ChatParticipantDto(
            userId = "u1",
            username = "alice",
            profilePictureUrl = "https://cdn.example.com/alice.png"
        )

        assertThat(dto.toDomain()).isEqualTo(
            ChatParticipant(
                userId = "u1",
                username = "alice",
                profilePictureUrl = "https://cdn.example.com/alice.png"
            )
        )
    }

    @Test
    fun `ChatParticipant round-trips through entity unchanged`() {
        val participant = ChatParticipant(
            userId = "u1",
            username = "alice",
            profilePictureUrl = null
        )

        assertThat(participant.toEntity().toDomain()).isEqualTo(participant)
        assertThat(participant.toEntity().profilePictureUrl).isNull()
    }

    @Test
    fun `ParticipantEntity maps to domain`() {
        val entity = ParticipantEntity(
            userId = "u2",
            username = "bob",
            profilePictureUrl = null
        )

        assertThat(entity.toDomain()).isEqualTo(
            ChatParticipant(userId = "u2", username = "bob", profilePictureUrl = null)
        )
    }

    @Test
    fun `participant initials are first two letters uppercased`() {
        val participant = ChatParticipant(
            userId = "u1",
            username = "alice",
            profilePictureUrl = null
        )

        assertThat(participant.initials).isEqualTo("AL")
    }
}
