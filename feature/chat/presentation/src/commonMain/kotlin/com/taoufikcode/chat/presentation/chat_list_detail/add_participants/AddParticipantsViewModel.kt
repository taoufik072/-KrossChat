package com.taoufikcode.chat.presentation.chat_list_detail.add_participants

import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taoufikcode.chat.domain.repository.ChatRepository
import com.taoufikcode.chat.presentation.chat_list_detail.participant_picker.ParticipantPickerAction
import com.taoufikcode.chat.presentation.chat_list_detail.participant_picker.ParticipantPickerState
import com.taoufikcode.chat.presentation.mappers.toUi
import com.taoufikcode.core.domain.util.DataError
import com.taoufikcode.core.domain.util.onFailure
import com.taoufikcode.core.domain.util.onSuccess
import com.taoufikcode.core.presentation.mapper.toUiText
import com.taoufikcode.core.presentation.utils.UiText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import krosschat.feature.chat.presentation.generated.resources.Res
import krosschat.feature.chat.presentation.generated.resources.error_participant_not_found
import kotlin.time.Duration.Companion.seconds

class AddParticipantsViewModel(
    private val chatRepository: ChatRepository
): ViewModel() {

    private val _chatId = MutableStateFlow<String?>(null)

    private val eventChannel = Channel<AddParticipantsEvent>()
    val events = eventChannel.receiveAsFlow()

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(ParticipantPickerState())
    @OptIn(ExperimentalCoroutinesApi::class)
    val state = _chatId
        .flatMapLatest { chatId ->
            if(chatId != null) {
                chatRepository.observeActiveParticipantsByChatId(chatId)
            } else emptyFlow()
        }
        .combine(_state) { participants, currentState ->
            currentState.copy(
                existingChatParticipants = participants.map { it.toUi() }
            )
        }
        .onStart {
            if (!hasLoadedInitialData) {
                searchFlow.launchIn(viewModelScope)
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ParticipantPickerState()
        )

    @OptIn(FlowPreview::class)
    private val searchFlow = snapshotFlow { _state.value.queryTextState.text.toString() }
        .debounce(1.seconds)
        .onEach { query ->
            performSearch(query)
        }

    fun onAction(action: ParticipantPickerAction) {
        when(action) {
            ParticipantPickerAction.OnAddClick -> addParticipant()
            ParticipantPickerAction.OnPrimaryActionClick -> addParticipantsToChat()
            is ParticipantPickerAction.ChatParticipants.OnClickChatMembers -> {
                _chatId.update { action.chatId }
            }
            else -> Unit
        }
    }

    private fun addParticipant() {
        state.value.currentSearchResult?.let { participantFromSearch ->
            val isAlreadySelected = state.value.selectedChatParticipants.any {
                it.id == participantFromSearch.id
            }
            val isAlreadyInChat = state.value.existingChatParticipants.any {
                it.id == participantFromSearch.id
            }
            val updatedParticipants = if(isAlreadyInChat || isAlreadySelected) {
                state.value.selectedChatParticipants
            } else state.value.selectedChatParticipants + participantFromSearch

            state.value.queryTextState.clearText()
            _state.update { it.copy(
                selectedChatParticipants = updatedParticipants,
                canAddParticipant = false,
                currentSearchResult = null
            ) }
        }
    }

    private fun addParticipantsToChat() {
        if(state.value.selectedChatParticipants.isEmpty()) {
            return
        }

        val chatId = _chatId.value ?: return

        val selectedParticipants = state.value.selectedChatParticipants
        val selectedUserIds = selectedParticipants.map { it.id }

        viewModelScope.launch {
            chatRepository
                .addParticipantsToChat(
                    chatId = chatId,
                    userIds = selectedUserIds
                )
                .onSuccess {
                    eventChannel.send(AddParticipantsEvent.OnMembersAdded)
                }
                .onFailure { error ->
                    _state.update { it.copy(
                        isSubmitting = false,
                        submitError = error.toUiText()
                    ) }
                }
        }
    }

    private fun performSearch(query: String) {
        if (query.isBlank()) {
            _state.update {
                it.copy(
                    currentSearchResult = null,
                    canAddParticipant = false,
                    searchError = null
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isSearching = true,
                    canAddParticipant = false
                )
            }

            chatRepository
                .searchParticipant(query)
                .onSuccess { participant ->
                    _state.update {
                        it.copy(
                            currentSearchResult = participant.toUi(),
                            isSearching = false,
                            canAddParticipant = true,
                            searchError = null
                        )
                    }
                }
                .onFailure { error ->
                    val errorMessage = when (error) {
                        DataError.Remote.NOT_FOUND -> UiText.Resource(Res.string.error_participant_not_found)
                        else -> error.toUiText()
                    }
                    _state.update {
                        it.copy(
                            searchError = errorMessage,
                            isSearching = false,
                            canAddParticipant = false,
                            currentSearchResult = null
                        )
                    }
                }
        }
    }
}