package com.taoufikcode.chat.presentation.chat_detail.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.taoufikcode.chat.domain.models.ChatMessageDeliveryStatus
import com.taoufikcode.chat.presentation.model.MessageUi
import com.taoufikcode.chat.presentation.util.getChatBubbleColorForUser
import com.taoufikcode.core.designsystem.components.avatar.ChatParticipantUi
import com.taoufikcode.core.designsystem.theme.KrossChatTheme
import com.taoufikcode.core.designsystem.theme.extended
import com.taoufikcode.core.presentation.utils.UiText
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun MessageListItemUi(
    messageUi: MessageUi,
    messageWithOpenMenu: MessageUi.CurrentUserMessage?,
    onMessageLongClick: (MessageUi.CurrentUserMessage) -> Unit,
    onDismissMessageMenu: () -> Unit,
    onDeleteClick: (MessageUi.CurrentUserMessage) -> Unit,
    onRetryClick: (MessageUi.CurrentUserMessage) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        when (messageUi) {
            is MessageUi.DateSeparator -> {
                DateSeparatorUi(
                    date = messageUi.date.asString(),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            is MessageUi.CurrentUserMessage -> {
                CurrentUserMessage(
                    message = messageUi,
                    messageWithOpenMenu = messageWithOpenMenu,
                    onMessageLongClick = { onMessageLongClick(messageUi) },
                    onDismissMessageMenu = onDismissMessageMenu,
                    onDeleteClick = { onDeleteClick(messageUi) },
                    onRetryClick = { onRetryClick(messageUi) }
                )
            }

            is MessageUi.OtherUserMessage -> {
                OtherUserMessage(
                    message = messageUi,
                    color = getChatBubbleColorForUser(messageUi.sender.id)
                )
            }
        }
    }
}


@Composable
private fun DateSeparatorUi(
    date: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f))
        Text(
            text = date,
            modifier = Modifier
                .padding(horizontal = 40.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.extended.textPlaceholder
        )
        HorizontalDivider(modifier = Modifier.weight(1f))
    }
}

@Composable
@Preview
fun MessageListItemLocalMessageUiPreview() {
    KrossChatTheme {
        MessageListItemUi(
            messageUi = MessageUi.CurrentUserMessage(
                id = "1",
                content = "Hello world, this is a preview message that spans multiple lines",
                deliveryStatus = ChatMessageDeliveryStatus.SENT,
                formattedSentTime = UiText.DynamicString("Friday 2:20pm")
            ),
            onRetryClick = {},
            onMessageLongClick = {},
            onDismissMessageMenu = {},
            onDeleteClick = {},
            messageWithOpenMenu = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
    }
}

@Composable
@Preview
fun MessageListItemLocalMessageRetryUiPreview() {
    KrossChatTheme {
        MessageListItemUi(
            messageUi = MessageUi.CurrentUserMessage(
                id = "1",
                content = "Hello world, this is a preview message that spans multiple lines",
                deliveryStatus = ChatMessageDeliveryStatus.FAILED,
                formattedSentTime = UiText.DynamicString("Friday 2:20pm")
            ),
            onRetryClick = {},
            onMessageLongClick = {},
            onDismissMessageMenu = {},
            onDeleteClick = {},
            messageWithOpenMenu = null,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@Composable
@Preview
fun MessageListItemOtherMessageUiPreview() {
    KrossChatTheme {
        MessageListItemUi(
            messageUi = MessageUi.OtherUserMessage(
                id = "1",
                content = "Hello world, this is a preview message that spans multiple lines",
                formattedSentTime = UiText.DynamicString("Friday 2:20pm"),
                sender = ChatParticipantUi(
                    id = "1",
                    username = "Taoufik",
                    initials = "TA"
                )
            ),
            onRetryClick = {},
            onMessageLongClick = {},
            onDismissMessageMenu = {},
            onDeleteClick = {},
            messageWithOpenMenu = null,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}