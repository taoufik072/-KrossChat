package com.taoufikcode.chat.presentation.chat_list_detail.add_participants

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.taoufikcode.chat.presentation.chat_list_detail.participant_picker.ParticipantPickerAction
import com.taoufikcode.chat.presentation.chat_list_detail.participant_picker.ParticipantPickerScreen
import com.taoufikcode.core.designsystem.components.dialogs.KrossAdaptiveDialogSheetLayout
import com.taoufikcode.core.presentation.utils.ObserveAsEvents
import krosschat.feature.chat.presentation.generated.resources.Res
import krosschat.feature.chat.presentation.generated.resources.chat_members
import krosschat.feature.chat.presentation.generated.resources.save
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddParticipantsRoot(
    chatId: String?,
    onDismiss: () -> Unit,
    onMembersAdded: () -> Unit,
    viewModel: AddParticipantsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is AddParticipantsEvent.OnMembersAdded -> onMembersAdded()
        }
    }
    LaunchedEffect(chatId) {
        viewModel.onAction(ParticipantPickerAction.ChatParticipants.OnClickChatMembers(chatId))
    }

    KrossAdaptiveDialogSheetLayout(
        onDismiss = onDismiss
    ) {
        ParticipantPickerScreen(
            headerText = stringResource(Res.string.chat_members),
            primaryButtonText = stringResource(Res.string.save),
            state = state,
            onAction = { action ->
                when (action) {
                    ParticipantPickerAction.OnDismissDialog -> onDismiss()
                    else -> Unit
                }
                viewModel.onAction(action)
            }
        )
    }
}
