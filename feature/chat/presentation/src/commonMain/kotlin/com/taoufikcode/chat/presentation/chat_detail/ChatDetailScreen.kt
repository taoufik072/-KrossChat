package com.taoufikcode.chat.presentation.chat_detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.taoufikcode.chat.domain.models.ChatMessage
import com.taoufikcode.chat.domain.models.ChatMessageDeliveryStatus
import com.taoufikcode.chat.presentation.chat_detail.components.ChatDetailHeader
import com.taoufikcode.chat.presentation.chat_detail.components.DateChip
import com.taoufikcode.chat.presentation.chat_detail.components.MessageBannerListener
import com.taoufikcode.chat.presentation.chat_detail.components.MessageBox
import com.taoufikcode.chat.presentation.chat_detail.components.MessageList
import com.taoufikcode.chat.presentation.chat_detail.components.PaginationScrollListener
import com.taoufikcode.chat.presentation.chat_list.components.EmptyListSection
import com.taoufikcode.chat.presentation.components.ChatHeader
import com.taoufikcode.chat.presentation.model.ChatUi
import com.taoufikcode.chat.presentation.model.MessageUi
import com.taoufikcode.core.designsystem.components.avatar.ChatParticipantUi
import com.taoufikcode.core.designsystem.components.chat.DynamicRoundedCornerColumn
import com.taoufikcode.core.designsystem.theme.KrossChatTheme
import com.taoufikcode.core.designsystem.theme.extended
import com.taoufikcode.core.presentation.utils.ObserveAsEvents
import com.taoufikcode.core.presentation.utils.UiText
import com.taoufikcode.core.presentation.utils.clearFocusOnTap
import com.taoufikcode.core.presentation.utils.currentDeviceConfiguration
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import krosschat.feature.chat.presentation.generated.resources.Res
import krosschat.feature.chat.presentation.generated.resources.no_chat_selected
import krosschat.feature.chat.presentation.generated.resources.select_a_chat
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChatDetailRoot(
    chatId: String?,
    isDetailPresent: Boolean,
    onBack: () -> Unit,
    onChatMembersClick: () -> Unit,
    viewModel: ChatDetailViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarState = remember { SnackbarHostState() }
    val messageListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            ChatDetailEvent.OnChatLeft -> onBack()
            is ChatDetailEvent.OnError -> {
                snackbarState.showSnackbar(event.error.asStringAsync())
            }

            is ChatDetailEvent.OnNewMessage -> {
                scope.launch {
                    messageListState.animateScrollToItem(0)
                }

            }
        }
    }
    LaunchedEffect(chatId) {
        viewModel.onAction(ChatDetailAction.OnSelectChat(chatId))
    }

    BackHandler(
        enabled = !isDetailPresent
    ) {
        viewModel.onAction(ChatDetailAction.OnSelectChat(null))
        onBack()
    }

    ChatDetailScreen(
        state = state,
        messageListState = messageListState,
        isDetailPresent = isDetailPresent,
        snackBarState = snackbarState,
        onAction = { action ->
            when (action) {
                is ChatDetailAction.OnChatMembersClick -> onChatMembersClick()
                is ChatDetailAction.OnBackClick -> onBack()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
fun ChatDetailScreen(
    state: ChatDetailState,
    messageListState: LazyListState,
    isDetailPresent: Boolean,
    snackBarState: SnackbarHostState,
    onAction: (ChatDetailAction) -> Unit,
) {
    val configuration = currentDeviceConfiguration()
    val realMessageItemCount = remember(state.messages) {
        state
            .messages
            .filter { it is MessageUi.CurrentUserMessage || it is MessageUi.OtherUserMessage }
            .size
    }
    LaunchedEffect(messageListState) {
        snapshotFlow {
            messageListState.firstVisibleItemIndex to messageListState.layoutInfo.totalItemsCount
        }.filter { (firstVisibleIndex, totalItemsCount) ->
            firstVisibleIndex >= 0 && totalItemsCount > 0
        }.collect { (firstVisibleItemIndex, _) ->
            onAction(ChatDetailAction.OnFirstVisibleIndexChanged(firstVisibleItemIndex))
        }
    }

    MessageBannerListener(
        lazyListState = messageListState,
        messages = state.messages,
        isBannerVisible = state.bannerState.isVisible,
        onShowBanner = { index ->
            onAction(ChatDetailAction.OnTopVisibleIndexChanged(index))
        },
        onHide = {
            onAction(ChatDetailAction.OnHideBanner)
        }
    )
    PaginationScrollListener(
        lazyListState = messageListState,
        itemCount = realMessageItemCount,
        isPaginationLoading = state.isPaginationLoading,
        isEndReached = state.endReached,
        onNearTop = {
            onAction(ChatDetailAction.OnScrollToTop)
        }
    )
    var headerHeight by remember {
        mutableStateOf(0.dp)
    }
    val density = LocalDensity.current
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = if (!configuration.isWideScreen) {
            MaterialTheme.colorScheme.surface
        } else {
            MaterialTheme.colorScheme.extended.surfaceLower
        },
        snackbarHost = {
            SnackbarHost(snackBarState)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.clearFocusOnTap().padding(innerPadding).then(
                if (configuration.isMobile) {
                    Modifier.padding(horizontal = 8.dp)
                } else Modifier
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DynamicRoundedCornerColumn(
                    isCornersRounded = configuration.isWideScreen,
                    modifier = Modifier.weight(1f).fillMaxWidth()
                ) {
                    if (state.chatUi == null) {
                        EmptyListSection(
                            modifier = Modifier.fillMaxSize(),
                            title = stringResource(Res.string.no_chat_selected),
                            description = stringResource(Res.string.select_a_chat),
                        )
                    } else {
                        ChatHeader(
                            modifier = Modifier
                                .onSizeChanged {
                                    headerHeight = with(density) {
                                        it.height.toDp()
                                    }
                                }
                        ) {
                            ChatDetailHeader(
                                chatUi = state.chatUi,
                                isDetailPresent = isDetailPresent,
                                isChatOptionsDropDownOpen = state.isChatOptionsOpen,
                                onChatOptionsClick = {
                                    onAction(ChatDetailAction.OnChatOptionsClick)
                                },
                                onDismissChatOptions = {
                                    onAction(ChatDetailAction.OnDismissChatOptions)
                                },
                                onChatMembersClick = {
                                    onAction(ChatDetailAction.OnChatMembersClick)
                                },
                                onLeaveChatClick = {
                                    onAction(ChatDetailAction.OnLeaveChatClick)
                                },
                                onBackClick = {
                                    onAction(ChatDetailAction.OnBackClick)
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        MessageList(
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            messages = state.messages,
                            messageWithOpenMenu = state.messageWithOpenMenu,
                            listState = messageListState,
                            isPaginationLoading = state.isPaginationLoading,
                            paginationError = state.paginationError?.asString(),
                            onMessageLongClick = { message ->
                                onAction(ChatDetailAction.OnMessageLongClick(message))
                            },
                            onMessageRetryClick = { message ->
                                onAction(ChatDetailAction.OnRetryClick(message))
                            },
                            onDismissMessageMenu = {
                                onAction(ChatDetailAction.OnDismissMessageMenu)
                            },
                            onDeleteMessageClick = { message ->
                                onAction(ChatDetailAction.OnDeleteMessageClick(message))
                            },
                            onRetryPaginationClick = {
                                onAction(ChatDetailAction.OnRetryPaginationClick)
                            },
                        )

                        AnimatedVisibility(
                            visible = !configuration.isWideScreen
                        ) {
                            DynamicRoundedCornerColumn(
                                isCornersRounded = configuration.isWideScreen
                            ) {
                                MessageBox(
                                    modifier = Modifier.fillMaxWidth()
                                        .imePadding()
                                        .padding(
                                            vertical = 8.dp,
                                            horizontal = 16.dp
                                        ),
                                    messageTextFieldState = state.messageTextFieldState,
                                    isSendButtonEnabled = state.canSendMessage,
                                    connectionState = state.connectionState,
                                    onSendClick = {
                                        onAction(ChatDetailAction.OnSendMessageClick)
                                    }
                                )
                            }
                        }
                    }

                    if (configuration.isWideScreen) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    AnimatedVisibility(
                        visible = configuration.isWideScreen && state.chatUi != null
                    ) {
                        DynamicRoundedCornerColumn(
                            isCornersRounded = configuration.isWideScreen
                        ) {
                            MessageBox(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .imePadding()
                                    .padding(8.dp),
                                messageTextFieldState = state.messageTextFieldState,
                                isSendButtonEnabled = state.canSendMessage,
                                connectionState = state.connectionState,
                                onSendClick = {
                                    onAction(ChatDetailAction.OnSendMessageClick)
                                }
                            )
                        }
                    }
                }
            }
            AnimatedVisibility(
                visible = state.bannerState.isVisible,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = headerHeight + 16.dp),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                if (state.bannerState.formattedDate != null) {
                    DateChip(
                        date = state.bannerState.formattedDate.asString()
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun ChatDetailEmptyPreview() {
    KrossChatTheme {
        ChatDetailScreen(
            state = ChatDetailState(),
            messageListState = rememberLazyListState(),
            isDetailPresent = false,
            snackBarState = remember { SnackbarHostState() },
            onAction = {})
    }
}

@OptIn(ExperimentalUuidApi::class)
@Preview
@Composable
private fun ChatDetailMessagesPreview() {
    KrossChatTheme(darkTheme = true) {
        ChatDetailScreen(
            state = ChatDetailState(
                messageTextFieldState = rememberTextFieldState(
                    initialText = "This is a new message!"
                ),
                canSendMessage = true,
                chatUi = ChatUi(
                    id = "1",
                    currentUser = ChatParticipantUi(
                        id = "1",
                        username = "Taoufik",
                        initials = "TA",
                    ),
                    otherParticipants = listOf(
                        ChatParticipantUi(
                            id = "2",
                            username = "Taoufik",
                            initials = "GH",
                        ), ChatParticipantUi(
                            id = "3",
                            username = "Taoufik",
                            initials = "TD",
                        )
                    ),
                    lastMessage = ChatMessage(
                        id = "1",
                        chatId = "1",
                        content = "lorem ipsum dolor sit amet consectetur adipiscing elit sed" + " do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                        createdAt = Clock.System.now(),
                        senderId = "1",
                        deliveryStatus = ChatMessageDeliveryStatus.SENT
                    ), lastMessageSenderUsername = "Taoufik"
                ),
                messages = (1..20).map {
                    if (it % 2 == 0) {
                        MessageUi.CurrentUserMessage(
                            id = Uuid.random().toString(),
                            content = "Hello world!",
                            deliveryStatus = ChatMessageDeliveryStatus.SENT,
                            formattedSentTime = UiText.DynamicString("Friday, Aug 20")
                        )
                    } else {
                        MessageUi.OtherUserMessage(
                            id = Uuid.random().toString(),
                            content = "Hello world!",
                            sender = ChatParticipantUi(
                                id = Uuid.random().toString(), username = "John", initials = "JO"
                            ),
                            formattedSentTime = UiText.DynamicString("Friday, Aug 20"),
                        )
                    }
                }),
            isDetailPresent = true,
            messageListState = rememberLazyListState(),
            snackBarState = remember { SnackbarHostState() },
            onAction = {}
        )
    }
}