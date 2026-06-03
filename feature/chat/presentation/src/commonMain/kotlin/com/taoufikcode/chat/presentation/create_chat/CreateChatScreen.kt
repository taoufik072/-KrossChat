package com.taoufikcode.chat.presentation.create_chat

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.taoufikcode.chat.domain.models.Chat
import com.taoufikcode.chat.presentation.components.ChatParticipantSearchTextSection
import com.taoufikcode.chat.presentation.components.ChatParticipantsSelectionSection
import com.taoufikcode.chat.presentation.components.ManageChatButtonSection
import com.taoufikcode.chat.presentation.components.ManageChatHeaderRow
import com.taoufikcode.core.designsystem.components.brand.KrossHorizontalDivider
import com.taoufikcode.core.designsystem.components.buttons.KrossButton
import com.taoufikcode.core.designsystem.components.buttons.KrossButtonStyle
import com.taoufikcode.core.designsystem.components.dialogs.KrossAdaptiveDialogSheetLayout
import com.taoufikcode.core.designsystem.theme.KrossChatTheme
import com.taoufikcode.core.presentation.utils.DeviceConfiguration
import com.taoufikcode.core.presentation.utils.ObserveAsEvents
import com.taoufikcode.core.presentation.utils.clearFocusOnTap
import com.taoufikcode.core.presentation.utils.currentDeviceConfiguration
import krosschat.feature.chat.presentation.generated.resources.Res
import krosschat.feature.chat.presentation.generated.resources.cancel
import krosschat.feature.chat.presentation.generated.resources.create_chat
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
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
        CreateChatScreen(
            state = state,
            onAction = { action ->
                when (action) {
                    CreateChatAction.OnDismissDialog -> onDismiss()
                    else -> Unit
                }
                viewModel.onAction(action)
            }
        )
    }
}

@Composable
fun CreateChatScreen(
    state: CreateChatState,
    onAction: (CreateChatAction) -> Unit,
) {
    var isTextFieldFocused by remember { mutableStateOf(false) }
    val imeHeight = WindowInsets.ime.getBottom(LocalDensity.current)
    val isKeyboardVisible = imeHeight > 0
    val configuration = currentDeviceConfiguration()

    val shouldHideHeader = configuration == DeviceConfiguration.MOBILE_LANDSCAPE
            || (isKeyboardVisible && configuration != DeviceConfiguration.DESKTOP) || isTextFieldFocused

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
                ManageChatHeaderRow(
                    title = stringResource(Res.string.create_chat),
                    onCloseClick = {
                        onAction(CreateChatAction.OnDismissDialog)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                KrossHorizontalDivider()
            }
        }
        ChatParticipantSearchTextSection(
            queryState = state.queryTextState,
            onAddClick = {
                onAction(CreateChatAction.OnAddClick)
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
        ChatParticipantsSelectionSection(
            selectedParticipants = state.selectedChatParticipants,
            modifier = Modifier
                .fillMaxWidth(),
            searchResult = state.currentSearchResult
        )
        KrossHorizontalDivider()
        ManageChatButtonSection(
            primaryButton = {
                KrossButton(
                    text = stringResource(Res.string.create_chat),
                    onClick = {
                        onAction(CreateChatAction.OnCreateChatClick)
                    },
                    enabled = state.selectedChatParticipants.isNotEmpty(),
                    isLoading = state.isCreatingChat
                )
            },
            secondaryButton = {
                KrossButton(
                    text = stringResource(Res.string.cancel),
                    onClick = {
                        onAction(CreateChatAction.OnDismissDialog)
                    },
                    style = KrossButtonStyle.SECONDARY
                )
            },
            error = state.createChatError?.asString(),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview
@Composable
private fun Preview() {
    KrossChatTheme {
        CreateChatScreen(
            state = CreateChatState(),
            onAction = {}
        )
    }
}