package com.taoufikcode.chat.data.mappers

import com.taoufikcode.chat.data.dto.ChatParticipantDto
import com.taoufikcode.chat.database.entities.MessageEntity
import com.taoufikcode.chat.database.entities.ParticipantEntity
import com.taoufikcode.chat.domain.models.ChatMessage
import com.taoufikcode.chat.domain.models.ChatMessageDeliveryStatus
import com.taoufikcode.chat.domain.models.ChatParticipant
import kotlin.time.Instant

fun ChatParticipantDto.toDomain(): ChatParticipant {
    return ChatParticipant(
        userId = userId,
        username = username,
        profilePictureUrl = profilePictureUrl
    )
}
fun MessageEntity.toDomain(): ChatMessage {
    return ChatMessage(
        id = chatId,
        chatId = chatId,
        content = content,
        createdAt = Instant.fromEpochMilliseconds(timestamp),
        senderId = senderId,
        deliveryStatus = ChatMessageDeliveryStatus.SENT
    )
}

fun ParticipantEntity.toDomain(): ChatParticipant {
    return ChatParticipant(
        userId = userId,
        username = username,
        profilePictureUrl = profilePictureUrl
    )
}

fun ChatParticipant.toEntity(): ParticipantEntity {
    return ParticipantEntity(
        userId = userId,
        username = username,
        profilePictureUrl = profilePictureUrl
    )
}