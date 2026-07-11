package com.taoufikcode.chat.presentation.chat_detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.taoufikcode.chat.domain.models.ChatMessage
import com.taoufikcode.chat.domain.models.ChatMessageDeliveryStatus
import com.taoufikcode.chat.presentation.components.ChatHeader
import com.taoufikcode.chat.presentation.components.ChatItemHeaderRow
import com.taoufikcode.chat.presentation.model.ChatUi
import com.taoufikcode.core.designsystem.components.avatar.ChatParticipantUi
import com.taoufikcode.core.designsystem.components.buttons.KrossIconButton
import com.taoufikcode.core.designsystem.components.dropdown.DropDownItem
import com.taoufikcode.core.designsystem.components.dropdown.KrossDropDownMenu
import com.taoufikcode.core.designsystem.theme.KrossChatTheme
import com.taoufikcode.core.designsystem.theme.extended
import krosschat.core.designsystem.generated.resources.arrow_left_icon
import krosschat.core.designsystem.generated.resources.dots_icon
import krosschat.core.designsystem.generated.resources.log_out_icon
import krosschat.core.designsystem.generated.resources.users_icon
import krosschat.feature.chat.presentation.generated.resources.Res
import krosschat.feature.chat.presentation.generated.resources.chat_members
import krosschat.feature.chat.presentation.generated.resources.go_back
import krosschat.feature.chat.presentation.generated.resources.leave_chat
import krosschat.feature.chat.presentation.generated.resources.open_chat_options_menu
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import kotlin.time.Clock
import krosschat.core.designsystem.generated.resources.Res as DesignSystemRes

@Composable
fun ChatDetailHeader(
    chatUi: ChatUi?,
    isDetailPresent: Boolean,
    isChatOptionsDropDownOpen: Boolean,
    onChatOptionsClick: () -> Unit,
    onDismissChatOptions: () -> Unit,
    onChatMembersClick: () -> Unit,
    onLeaveChatClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (!isDetailPresent) {
            KrossIconButton(
                onClick = onBackClick
            ) {
                Icon(
                    imageVector = vectorResource(DesignSystemRes.drawable.arrow_left_icon),
                    contentDescription = stringResource(Res.string.go_back),
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.extended.textSecondary
                )
            }
        }

        if (chatUi != null) {
            val isGroupChat = chatUi.otherParticipants.size > 1
            ChatItemHeaderRow(
                chat = chatUi,
                isGroupChat = isGroupChat,
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        onChatMembersClick()
                    }
            )
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }

        Box {
            KrossIconButton(
                onClick = onChatOptionsClick
            ) {
                Icon(
                    imageVector = vectorResource(DesignSystemRes.drawable.dots_icon),
                    contentDescription = stringResource(Res.string.open_chat_options_menu),
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.extended.textSecondary
                )
            }

            KrossDropDownMenu(
                isOpen = isChatOptionsDropDownOpen,
                onDismiss = onDismissChatOptions,
                items = listOf(
                    DropDownItem(
                        title = stringResource(Res.string.chat_members),
                        icon = vectorResource(DesignSystemRes.drawable.users_icon),
                        contentColor = MaterialTheme.colorScheme.extended.textSecondary,
                        onClick = onChatMembersClick
                    ),
                    DropDownItem(
                        title = stringResource(Res.string.leave_chat),
                        icon = vectorResource(DesignSystemRes.drawable.log_out_icon),
                        contentColor = MaterialTheme.colorScheme.extended.destructiveHover,
                        onClick = onLeaveChatClick
                    ),
                )
            )
        }
    }
}

@Composable
@Preview
fun ChatDetailHeaderPreview() {
    KrossChatTheme(darkTheme = true) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .fillMaxSize()
        ) {
            ChatHeader {
                ChatDetailHeader(
                    isDetailPresent = false,
                    isChatOptionsDropDownOpen = false,
                    chatUi = ChatUi(
                        id = "1",
                        currentUser = ChatParticipantUi(
                            id = "1",
                            username = "Taoufik",
                            initials = "PH",
                        ),
                        otherParticipants = listOf(
                            ChatParticipantUi(
                                id = "2",
                                username = "Taoufik",
                                initials = "CI",
                            )
                        ),
                        lastMessage = ChatMessage(
                            id = "1",
                            chatId = "1",
                            content = "lorem ipsum dolor sit amet consectetur adipiscing elit sed" +
                                    "do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                            createdAt = Clock.System.now(),
                            senderId = "1",
                            deliveryStatus = ChatMessageDeliveryStatus.SENT
                        ),
                        lastMessageSenderUsername = "Taoufik"
                    ),
                    onChatOptionsClick = {},
                    onChatMembersClick = {},
                    onLeaveChatClick = {},
                    onDismissChatOptions = {},
                    onBackClick = {},
                )
            }
        }
    }
}

@Composable
@Preview
fun ChatDetailHeaderGroupPreview() {
    KrossChatTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .fillMaxSize()
        ) {
            ChatHeader {
                ChatDetailHeader(
                    isDetailPresent = false,
                    isChatOptionsDropDownOpen = true,
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
                            ),
                            ChatParticipantUi(
                                id = "3",
                                username = "Taoufik",
                                initials = "TD",
                            )
                        ),
                        lastMessage = ChatMessage(
                            id = "1",
                            chatId = "1",
                            content = "This is a last chat message that was sent by Taoufik " +
                                    "and goes over multiple lines to showcase the ellipsis",
                            createdAt = Clock.System.now(),
                            senderId = "1",
                            deliveryStatus = ChatMessageDeliveryStatus.SENT
                        ),
                        lastMessageSenderUsername = "Taoufik"
                    ),
                    onChatOptionsClick = {},
                    onChatMembersClick = {},
                    onLeaveChatClick = {},
                    onDismissChatOptions = {},
                    onBackClick = {},
                )
            }
        }
    }
}