package com.taoufikcode.chat.presentation.create_chat

import androidx.compose.foundation.text.input.TextFieldState
import com.taoufikcode.core.designsystem.components.avatar.ChatParticipantUi
import com.taoufikcode.core.presentation.utils.UiText

data class CreateChatState(
    val queryTextState: TextFieldState = TextFieldState(),
    val selectedChatParticipants: List<ChatParticipantUi> = emptyList(),
    val isAddingParticipant: Boolean = false,
    val isSearching: Boolean = false,
    val canAddParticipant: Boolean = false,
    val isLoadingParticipants: Boolean = false,
    val currentSearchResult: ChatParticipantUi? = null,
    val searchError: UiText? = null,
    val isCreatingChat: Boolean = false,
)