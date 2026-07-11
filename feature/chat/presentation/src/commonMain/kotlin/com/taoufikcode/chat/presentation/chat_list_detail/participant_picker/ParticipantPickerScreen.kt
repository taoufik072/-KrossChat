package com.taoufikcode.chat.presentation.chat_list_detail.participant_picker

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import com.taoufikcode.chat.presentation.chat_list_detail.participant_picker.components.ParticipantPickerButtonSection
import com.taoufikcode.chat.presentation.chat_list_detail.participant_picker.components.ParticipantPickerHeaderRow
import com.taoufikcode.chat.presentation.chat_list_detail.participant_picker.components.ParticipantSearchSection
import com.taoufikcode.chat.presentation.chat_list_detail.participant_picker.components.ParticipantsSelectionSection
import com.taoufikcode.core.designsystem.components.avatar.ChatParticipantUi
import com.taoufikcode.core.designsystem.components.brand.KrossHorizontalDivider
import com.taoufikcode.core.designsystem.components.buttons.KrossButton
import com.taoufikcode.core.designsystem.components.buttons.KrossButtonStyle
import com.taoufikcode.core.designsystem.theme.KrossChatTheme
import com.taoufikcode.core.presentation.utils.DeviceConfiguration
import com.taoufikcode.core.presentation.utils.clearFocusOnTap
import com.taoufikcode.core.presentation.utils.currentDeviceConfiguration
import krosschat.feature.chat.presentation.generated.resources.Res
import krosschat.feature.chat.presentation.generated.resources.cancel
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ParticipantPickerScreen(
    headerText: String,
    primaryButtonText: String,
    state: ParticipantPickerState,
    onAction: (ParticipantPickerAction) -> Unit,
) {
    var isTextFieldFocused by remember { mutableStateOf(false) }
    val imeHeight = WindowInsets.ime.getBottom(LocalDensity.current)
    val isKeyboardVisible = imeHeight > 0
    val configuration = currentDeviceConfiguration()

    val shouldHideHeader = configuration == DeviceConfiguration.MOBILE_LANDSCAPE
            || isKeyboardVisible || isTextFieldFocused
    Column(
        modifier = Modifier
            .clearFocusOnTap()
            .fillMaxWidth()
            .wrapContentHeight()
            .imePadding()
            .background(MaterialTheme.colorScheme.surface)
            .navigationBarsPadding()
    ) {
        AnimatedVisibility(
            visible = !shouldHideHeader
        ) {
            Column {
                ParticipantPickerHeaderRow(
                    title = headerText,
                    onCloseClick = {
                        onAction(ParticipantPickerAction.OnDismissDialog)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                KrossHorizontalDivider()
            }
        }
        ParticipantSearchSection(
            queryState = state.queryTextState,
            onAddClick = {
                onAction(ParticipantPickerAction.OnAddClick)
            },
            isSearchEnabled = state.canAddParticipant,
            isLoading = state.isSearching,
            modifier = Modifier
                .fillMaxWidth(),
            error = state.searchError,
            onFocusChanged = {
                isTextFieldFocused = it
            }
        )
        KrossHorizontalDivider()
        ParticipantsSelectionSection(
            existingParticipants = state.existingChatParticipants,
            selectedParticipants = state.selectedChatParticipants,
            modifier = Modifier
                .fillMaxWidth(),
            searchResult = state.currentSearchResult
        )
        KrossHorizontalDivider()
        ParticipantPickerButtonSection(
            primaryButton = {
                KrossButton(
                    text = primaryButtonText,
                    onClick = {
                        onAction(ParticipantPickerAction.OnPrimaryActionClick)
                    },
                    enabled = state.selectedChatParticipants.isNotEmpty(),
                    isLoading = state.isSubmitting
                )
            },
            secondaryButton = {
                KrossButton(
                    text = stringResource(Res.string.cancel),
                    onClick = {
                        onAction(ParticipantPickerAction.OnDismissDialog)
                    },
                    style = KrossButtonStyle.SECONDARY
                )
            },
            error = state.submitError?.asString(),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview
@Composable
private fun ParticipantPickerScreenPreview() {
    KrossChatTheme {
        ParticipantPickerScreen(
            headerText = "Create chat",
            primaryButtonText = "Create chat",
            state = ParticipantPickerState(
                selectedChatParticipants = listOf(
                    ChatParticipantUi(
                        id = "1",
                        username = "John Doe",
                        initials = "JD",
                    ),
                    ChatParticipantUi(
                        id = "2",
                        username = "Jane Smith",
                        initials = "JS",
                    )
                )
            ),
            onAction = {}
        )
    }
}

@Preview
@Composable
private fun ParticipantPickerScreenSearchingPreview() {
    KrossChatTheme(darkTheme = true) {
        ParticipantPickerScreen(
            headerText = "Add participant",
            primaryButtonText = "Add",
            state = ParticipantPickerState(
                isSearching = true,
                currentSearchResult = ChatParticipantUi(
                    id = "3",
                    username = "Alice Wonder",
                    initials = "AW",
                ),
                canAddParticipant = true
            ),
            onAction = {}
        )
    }
}
