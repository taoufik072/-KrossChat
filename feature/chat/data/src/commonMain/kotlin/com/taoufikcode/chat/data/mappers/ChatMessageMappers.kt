package com.taoufikcode.chat.data.mappers

import com.taoufikcode.chat.data.dto.ChatMessageDto
import com.taoufikcode.chat.domain.models.ChatMessage
import kotlin.time.Instant

fun ChatMessageDto.toDomain(): ChatMessage {
    return ChatMessage(
        id = id,
        chatId = chatId,
        content = content,
        createdAt = Instant.parse(createdAt),
        senderId = senderId
    )
}