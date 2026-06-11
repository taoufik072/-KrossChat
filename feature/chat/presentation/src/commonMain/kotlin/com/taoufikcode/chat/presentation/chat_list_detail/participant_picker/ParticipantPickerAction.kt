package com.taoufikcode.chat.presentation.chat_list_detail.participant_picker

sealed interface ParticipantPickerAction {
    data object OnAddClick : ParticipantPickerAction
    data object OnDismissDialog : ParticipantPickerAction
    data object OnPrimaryActionClick : ParticipantPickerAction
    sealed interface ChatParticipants : ParticipantPickerAction {
        data class OnClickChatMembers(val chatId: String?) : ParticipantPickerAction
    }
}
