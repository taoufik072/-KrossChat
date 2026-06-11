package com.taoufikcode.chat.presentation.chat_list_detail.create_chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.taoufikcode.chat.domain.models.Chat
import com.taoufikcode.chat.presentation.chat_list_detail.participant_picker.ParticipantPickerAction
import com.taoufikcode.chat.presentation.chat_list_detail.participant_picker.ParticipantPickerScreen
import com.taoufikcode.core.designsystem.components.dialogs.KrossAdaptiveDialogSheetLayout
import com.taoufikcode.core.presentation.utils.ObserveAsEvents
import krosschat.feature.chat.presentation.generated.resources.Res
import krosschat.feature.chat.presentation.generated.resources.create_chat
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CreateChatRoot(
    onDismiss: () -> Unit,
    onChatCreated: (Chat) -> Unit,
    viewModel: CreateChatViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is CreateChatEvent.OnChatCreated -> onChatCreated(event.chat)
        }
    }

    KrossAdaptiveDialogSheetLayout(
        onDismiss = onDismiss
    ) {
        ParticipantPickerScreen(
            headerText = stringResource(Res.string.create_chat),
            primaryButtonText = stringResource(Res.string.create_chat),
            state = state,
            onAction = { action ->
                when (action) {
                    ParticipantPickerAction.OnDismissDialog -> onDismiss()
                    else -> Unit
                }
                viewModel.onAction(action)
            })
    }
}
