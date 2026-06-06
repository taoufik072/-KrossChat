@file:OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalComposeUiApi::class)

package com.taoufikcode.chat.presentation.chat_list_detail

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.taoufikcode.chat.presentation.chat_detail.ChatDetailRoot
import com.taoufikcode.chat.presentation.chat_list.ChatListRoot
import com.taoufikcode.chat.presentation.create_chat.CreateChatRoot
import com.taoufikcode.core.designsystem.theme.extended
import com.taoufikcode.core.presentation.utils.DialogSheetScopedViewModel
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ChatListDetailRoot(
    onLogout: () -> Unit,
    viewModel: ChatListDetailViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ChatListDetailScreen(
        state = state,
        onAction = viewModel::onAction,
        onLogout = onLogout
    )
}

@Composable
fun ChatListDetailScreen(
    state: ChatListDetailState,
    onAction: (ChatListDetailAction) -> Unit,
    onLogout: () -> Unit
) {
    val scaffoldDirective = krossPaneScaffoldDirective()
    val scaffoldNavigator = rememberListDetailPaneScaffoldNavigator(
        scaffoldDirective = scaffoldDirective
    )
    val scope = rememberCoroutineScope()

    BackHandler(enabled = scaffoldNavigator.canNavigateBack()) {
        scope.launch { scaffoldNavigator.navigateBack() }
    }

    val detailPane = scaffoldNavigator.scaffoldValue[ListDetailPaneScaffoldRole.Detail]
    LaunchedEffect(detailPane, state.selectedChatId) {
        if (detailPane == PaneAdaptedValue.Hidden && state.selectedChatId != null) {
            onAction(ChatListDetailAction.OnChatClick(null))
        }
    }

    ListDetailPaneScaffold(
        directive = scaffoldDirective,
        value = scaffoldNavigator.scaffoldValue,
        modifier = Modifier.background(MaterialTheme.colorScheme.extended.surfaceLower),
        listPane = {
            AnimatedPane {
                ChatListRoot(
                    onChatClick = {
                        onAction(ChatListDetailAction.OnChatClick(it.id))
                        scope.launch {
                            scaffoldNavigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
                        }
                    },
                    onConfirmLogoutClick = onLogout,
                    onCreateChatClick = {
                        onAction(ChatListDetailAction.OnCreateChatClick)
                    },
                    onProfileSettingsClick = {
                        onAction(ChatListDetailAction.OnProfileSettingsClick)
                    },
                )
            }
        },
        detailPane = {
            AnimatedPane {
                AnimatedPane {
                    val listPane = scaffoldNavigator.scaffoldValue[ListDetailPaneScaffoldRole.List]
                    ChatDetailRoot(
                        chatId = state.selectedChatId,
                        isDetailPresent = detailPane == PaneAdaptedValue.Expanded && listPane == PaneAdaptedValue.Expanded,
                        onBack = {
                            scope.launch {
                                if (scaffoldNavigator.canNavigateBack()) {
                                    scaffoldNavigator.navigateBack()
                                }
                            }
                        }
                    )
                }
            }
            DialogSheetScopedViewModel(
                visible = state.dialogState is DialogState.CreateChat
            ) {
                CreateChatRoot(
                    onChatCreated = { chat ->
                        onAction(ChatListDetailAction.OnDismissCurrentDialog)
                        onAction(ChatListDetailAction.OnChatClick(chat.id))
                        scope.launch {
                            scaffoldNavigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
                        }
                    },
                    onDismiss = {
                        onAction(ChatListDetailAction.OnDismissCurrentDialog)
                    }
                )
            }
        }
    )
}
