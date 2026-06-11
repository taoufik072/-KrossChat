package com.taoufikcode.chat.presentation.chat_list_detail.add_participants

sealed interface AddParticipantsEvent {
    data object OnMembersAdded: AddParticipantsEvent
}
