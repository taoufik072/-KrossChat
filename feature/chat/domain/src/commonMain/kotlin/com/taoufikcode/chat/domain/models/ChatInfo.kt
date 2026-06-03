package com.taoufikcode.chat.domain.models

data class ChatInfo(
    val chat: Chat,
    val messages: List<MessageWithSender>
)
