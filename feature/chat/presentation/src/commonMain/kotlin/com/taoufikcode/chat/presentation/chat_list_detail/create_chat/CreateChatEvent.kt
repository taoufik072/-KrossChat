package com.taoufikcode.chat.presentation.chat_list_detail.create_chat

import com.taoufikcode.chat.domain.models.Chat

sealed interface CreateChatEvent {
    data class OnChatCreated(val chat: Chat) : CreateChatEvent
}