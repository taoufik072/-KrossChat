package com.taoufikcode.chat.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateChatDto(
    val otherUserIds: List<String>
)
