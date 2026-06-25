package com.taoufikcode.chat.presentation.chat_list_detail.create_chat

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
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
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

class CreateChatViewModel(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private var hasLoadedInitialData = false

    private val eventChannel = Channel<CreateChatEvent>()
    val events = eventChannel.receiveAsFlow()

    private val _state = MutableStateFlow(ParticipantPickerState())

    @OptIn(FlowPreview::class)
    private val searchFlow =
        snapshotFlow { _state.value.queryTextState.text.toString() }
            .debounce(1.seconds)
            .onEach { query ->
                performSearch(query)
            }

    val state = _state.onStart {
            if (!hasLoadedInitialData) {
                searchFlow.launchIn(viewModelScope)
                hasLoadedInitialData = true
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ParticipantPickerState()
        )

    fun onAction(action: ParticipantPickerAction) {
        when (action) {
            ParticipantPickerAction.OnAddClick -> addParticipant()
            ParticipantPickerAction.OnPrimaryActionClick -> createChat()
            else -> Unit
        }
    }

    private fun createChat() {
        val userIds = state.value.selectedChatParticipants.map { it.id }
        if (userIds.isEmpty()) {
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isSubmitting = true, canAddParticipant = false
                )
            }

            chatRepository.createChat(userIds).onSuccess { chat ->
                    _state.update {
                        it.copy(
                            isSubmitting = false
                        )
                    }
                    eventChannel.send(CreateChatEvent.OnChatCreated(chat))
                }.onFailure { error ->
                    _state.update {
                        it.copy(
                            submitError = error.toUiText(),
                            canAddParticipant = it.currentSearchResult != null && !it.isSearching,
                            isSubmitting = false
                        )
                    }
                }
        }
    }

    private fun addParticipant() {
        state.value.currentSearchResult?.let { participant ->
            val isAlreadyPartOfChat = state.value.selectedChatParticipants.any {
                it.id == participant.id
            }
            if (!isAlreadyPartOfChat) {
                _state.update {
                    it.copy(
                        selectedChatParticipants = it.selectedChatParticipants + participant,
                        canAddParticipant = false,
                        currentSearchResult = null
                    )
                }
                _state.value.queryTextState.clearText()
            }
        }
    }

    private fun performSearch(query: String) {
        if (query.isBlank()) {
            _state.update {
                it.copy(
                    currentSearchResult = null, canAddParticipant = false, searchError = null
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isSearching = true, canAddParticipant = false
                )
            }

            chatRepository.searchParticipant(query).onSuccess { participant ->
                    _state.update {
                        it.copy(
                            currentSearchResult = participant.toUi(),
                            isSearching = false,
                            canAddParticipant = true,
                            searchError = null
                        )
                    }
                }.onFailure { error ->
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
