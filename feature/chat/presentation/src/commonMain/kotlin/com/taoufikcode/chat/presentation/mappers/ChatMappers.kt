package com.taoufikcode.chat.presentation.mappers

import com.taoufikcode.chat.domain.models.Chat
import com.taoufikcode.chat.presentation.model.ChatUi

fun Chat.toUi(currentUserId: String): ChatUi {
    val (current, other) = participants.partition { it.userId == currentUserId }
    return ChatUi(
        id = id,
        currentUser = current.first().toUi(),
        otherParticipants = other.map { it.toUi() },
        lastMessage = lastMessage,
        lastMessageSenderUsername = participants
            .find { it.userId == lastMessage?.senderId }
            ?.username,
        unreadCount = unreadCount
    )
}