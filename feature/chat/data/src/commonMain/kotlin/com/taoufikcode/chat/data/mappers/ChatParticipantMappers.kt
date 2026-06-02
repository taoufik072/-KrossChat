package com.taoufikcode.chat.data.mappers

import com.taoufikcode.chat.data.dto.ChatParticipantDto
import com.taoufikcode.chat.domain.models.ChatParticipant

fun ChatParticipantDto.toDomain(): ChatParticipant {
    return ChatParticipant(
        userId = userId,
        username = username,
        profilePictureUrl = profilePictureUrl
    )
}