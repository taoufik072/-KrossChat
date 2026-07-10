package com.taoufikcode.chat.presentation.chat_list

import com.taoufikcode.core.presentation.utils.UiText


sealed interface ChatListEvent {
    data object OnLogoutSuccess: ChatListEvent
    data class OnLogoutError(val error: UiText): ChatListEvent
}