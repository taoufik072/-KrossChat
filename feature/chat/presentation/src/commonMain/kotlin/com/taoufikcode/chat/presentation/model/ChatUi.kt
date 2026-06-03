package com.taoufikcode.chat.presentation.model

import androidx.compose.runtime.Immutable
import com.taoufikcode.chat.domain.models.ChatMessage
import com.taoufikcode.core.designsystem.components.avatar.ChatParticipantUi

@Immutable
data class ChatUi(
    val id: String,
    val currentUser: ChatParticipantUi,
    val otherParticipants: List<ChatParticipantUi>,
    val lastMessage: ChatMessage?,
    val lastMessageSenderUsername: String?
)
