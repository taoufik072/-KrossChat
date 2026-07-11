package com.taoufikcode.chat.presentation.chat_list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.taoufikcode.chat.presentation.chat_list.components.ChatListHeader
import com.taoufikcode.chat.presentation.chat_list.components.ChatListItemUi
import com.taoufikcode.chat.presentation.chat_list.components.EmptyListSection
import com.taoufikcode.core.designsystem.components.brand.KrossHorizontalDivider
import com.taoufikcode.core.designsystem.components.buttons.KrossFloatingActionButton
import com.taoufikcode.core.designsystem.components.dialogs.DestructiveConfirmationDialog
import com.taoufikcode.core.designsystem.theme.KrossChatTheme
import com.taoufikcode.core.designsystem.theme.extended
import com.taoufikcode.core.presentation.permissions.Permissions
import com.taoufikcode.core.presentation.permissions.rememberPermissionController
import com.taoufikcode.core.presentation.utils.ObserveAsEvents
import kotlinx.coroutines.launch
import krosschat.feature.chat.presentation.generated.resources.Res
import krosschat.feature.chat.presentation.generated.resources.cancel
import krosschat.feature.chat.presentation.generated.resources.create_chat
import krosschat.feature.chat.presentation.generated.resources.do_you_want_to_logout
import krosschat.feature.chat.presentation.generated.resources.do_you_want_to_logout_desc
import krosschat.feature.chat.presentation.generated.resources.logout
import krosschat.feature.chat.presentation.generated.resources.no_chats
import krosschat.feature.chat.presentation.generated.resources.no_chats_subtitle
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ChatListRoot(
    selectedChatId: String?,
    onChatClick: (String?) -> Unit,
    onCreateChatClick: () -> Unit,
    onProfileSettingsClick: () -> Unit,
    onSuccessfulLogout: () -> Unit,
    viewModel: ChatListViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val permissionController = rememberPermissionController()

    LaunchedEffect(selectedChatId) {
        viewModel.onAction(ChatListAction.OnSelectChat(selectedChatId))
    }
    LaunchedEffect(true) {
        permissionController.requestPermission(Permissions.NOTIFICATIONS)
    }

    val scope = rememberCoroutineScope()
    ObserveAsEvents(viewModel.events) { event ->
        when(event) {
            is ChatListEvent.OnLogoutError -> {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = event.error.asStringAsync()
                    )
                }
            }
            ChatListEvent.OnLogoutSuccess -> onSuccessfulLogout()
        }
    }

    ChatListScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is ChatListAction.OnSelectChat -> onChatClick(action.chatId)
                ChatListAction.OnCreateChatClick -> onCreateChatClick()
                ChatListAction.OnProfileSettingsClick -> onProfileSettingsClick()
                else -> Unit
            }
            viewModel.onAction(action)
        },
        snackbarHostState = snackbarHostState
    )
}

@Composable
fun ChatListScreen(
    state: ChatListState, onAction: (ChatListAction) -> Unit, snackbarHostState: SnackbarHostState
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.extended.surfaceLower,
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            KrossFloatingActionButton(
                onClick = {
                    onAction(ChatListAction.OnCreateChatClick)
                }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(Res.string.create_chat)
                )
            }
        }) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ChatListHeader(
                currentUser = state.currentUser,
                isUserMenuOpen = state.isUserMenuOpen,
                onUserAvatarClick = {
                    onAction(ChatListAction.OnUserAvatarClick)
                },
                onLogoutClick = {
                    onAction(ChatListAction.OnLogoutClick)
                },
                onDismissMenu = {
                    onAction(ChatListAction.OnDismissUserMenu)
                },
                onProfileSettingsClick = {
                    onAction(ChatListAction.OnProfileSettingsClick)
                })
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                state.chats.isEmpty() -> {
                    EmptyListSection(
                        modifier = Modifier.weight(1f).fillMaxWidth().padding(
                            horizontal = 8.dp
                        ),
                        title = stringResource(Res.string.no_chats),
                        description = stringResource(Res.string.no_chats_subtitle),

                        )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().weight(1f)
                    ) {
                        items(
                            items = state.chats, key = { it.id }) { chatUi ->
                            ChatListItemUi(
                                chat = chatUi,
                                isSelected = chatUi.id == state.selectedChatId,
                                modifier = Modifier.fillMaxWidth().clickable {
                                    onAction(ChatListAction.OnSelectChat(chatUi.id))
                                })
                            KrossHorizontalDivider()
                        }
                    }
                }
            }
        }
    }

    if (state.showLogoutConfirmation) {
        DestructiveConfirmationDialog(
            title = stringResource(Res.string.do_you_want_to_logout),
            description = stringResource(Res.string.do_you_want_to_logout_desc),
            confirmButtonText = stringResource(Res.string.logout),
            cancelButtonText = stringResource(Res.string.cancel),
            onDismiss = {
                onAction(ChatListAction.OnDismissLogoutDialog)
            },
            onCancelClick = {
                onAction(ChatListAction.OnDismissLogoutDialog)
            },
            onConfirmClick = {
                onAction(ChatListAction.OnConfirmLogout)
            },
        )
    }
}

@Preview
@Composable
private fun Preview() {
    KrossChatTheme {
        ChatListScreen(
            state = ChatListState(),
            onAction = {},
            snackbarHostState = remember { SnackbarHostState() })
    }
}