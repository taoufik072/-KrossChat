package com.taoufikcode.chat.presentation.chat_detail

import com.taoufikcode.core.presentation.utils.UiText

sealed interface ChatDetailEvent {
    data object OnChatLeft: ChatDetailEvent
    data class OnError(val error: UiText): ChatDetailEvent

    data object OnNewMessage: ChatDetailEvent

}