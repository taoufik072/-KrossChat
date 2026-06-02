package com.taoufikcode.chat.presentation.mappers

import com.taoufikcode.chat.domain.models.ChatParticipant
import com.taoufikcode.core.designsystem.components.avatar.ChatParticipantUi

fun ChatParticipant.toUi(): ChatParticipantUi {
    return ChatParticipantUi(
        id = userId,
        username = username,
        initials = initials,
        imageUrl = profilePictureUrl
    )
}