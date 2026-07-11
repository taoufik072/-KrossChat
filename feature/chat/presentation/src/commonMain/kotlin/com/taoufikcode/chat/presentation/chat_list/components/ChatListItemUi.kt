package com.taoufikcode.chat.presentation.chat_list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.taoufikcode.chat.domain.models.ChatMessage
import com.taoufikcode.chat.domain.models.ChatMessageDeliveryStatus
import androidx.compose.ui.Alignment
import com.taoufikcode.chat.presentation.components.ChatItemHeaderRow
import com.taoufikcode.chat.presentation.model.ChatUi
import com.taoufikcode.core.designsystem.components.avatar.ChatParticipantUi
import com.taoufikcode.core.designsystem.components.badge.KrossCountBadge
import com.taoufikcode.core.designsystem.theme.KrossChatTheme
import com.taoufikcode.core.designsystem.theme.extended
import androidx.compose.ui.tooling.preview.Preview
import kotlin.time.Clock

@Composable
fun ChatListItemUi(
    chat: ChatUi,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    val isGroupChat = chat.otherParticipants.size > 1

    Row(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .background(
                color = if (isSelected) {
                    MaterialTheme.colorScheme.surface
                } else {
                    MaterialTheme.colorScheme.extended.surfaceLower
                }
            )
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ChatItemHeaderRow(
                chat = chat,
                isGroupChat = isGroupChat,
                modifier = Modifier
                    .fillMaxWidth()
            )

            chat.lastMessage?.let { lastMessage ->
                val previewMessage = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.extended.textSecondary,
                        )
                    ) {
                        append(chat.lastMessageSenderUsername + ":")
                    }
                    append(lastMessage.content)
                }
                Text(
                    text = previewMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.extended.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        if (chat.unreadCount > 0) {
            KrossCountBadge(
                count = chat.unreadCount,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 12.dp)
            )
        }
        Box(
            modifier = Modifier
                .alpha(if (isSelected) 1f else 0f)
                .background(MaterialTheme.colorScheme.primary)
                .width(4.dp)
                .fillMaxHeight()
        )
    }
}

@Composable
@Preview
fun ChatListItemUiPreview() {
    KrossChatTheme(darkTheme = true) {
        ChatListItemUi(
            isSelected = true,
            modifier = Modifier
                .fillMaxWidth(),
            chat = ChatUi(
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
                    content = "Lorem Ipsum is simply dummy text of the printing and typesetting industry." +
                            " Lorem Ipsum has been the industry's standard dummy text ever since 1966",
                    createdAt = Clock.System.now(),
                    senderId = "1",
                    deliveryStatus = ChatMessageDeliveryStatus.SENT
                ),
                lastMessageSenderUsername = "Taoufik"
            )
        )
    }
}