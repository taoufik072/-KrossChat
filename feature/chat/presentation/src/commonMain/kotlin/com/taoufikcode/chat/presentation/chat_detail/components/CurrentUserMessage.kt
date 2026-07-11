package com.taoufikcode.chat.presentation.chat_detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.taoufikcode.chat.domain.models.ChatMessageDeliveryStatus
import com.taoufikcode.chat.presentation.model.MessageUi
import com.taoufikcode.core.designsystem.components.chat.KrossChatBubble
import com.taoufikcode.core.designsystem.components.chat.TrianglePosition
import com.taoufikcode.core.designsystem.components.dropdown.DropDownItem
import com.taoufikcode.core.designsystem.components.dropdown.KrossDropDownMenu
import com.taoufikcode.core.designsystem.theme.KrossChatTheme
import com.taoufikcode.core.designsystem.theme.extended
import com.taoufikcode.core.presentation.utils.UiText
import krosschat.feature.chat.presentation.generated.resources.Res
import krosschat.feature.chat.presentation.generated.resources.delete_for_everyone
import krosschat.feature.chat.presentation.generated.resources.reload_icon
import krosschat.feature.chat.presentation.generated.resources.retry
import krosschat.feature.chat.presentation.generated.resources.you
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun CurrentUserMessage(
    message: MessageUi.CurrentUserMessage,
    messageWithOpenMenu: MessageUi.CurrentUserMessage?,
    onMessageLongClick: () -> Unit,
    onDismissMessageMenu: () -> Unit,
    onDeleteClick: () -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
    ) {
        Box {
            KrossChatBubble(
                messageContent = message.content,
                sender = stringResource(Res.string.you),
                formattedDateTime = message.formattedSentTime.asString(),
                trianglePosition = TrianglePosition.RIGHT,
                messageStatus = {
                    MessageStatus(
                        status = message.deliveryStatus
                    )
                },
                onLongClick = {
                    onMessageLongClick()
                }
            )

            KrossDropDownMenu(
                isOpen = message.id == messageWithOpenMenu?.id,
                onDismiss = onDismissMessageMenu,
                items = listOf(
                    DropDownItem(
                        title = stringResource(Res.string.delete_for_everyone),
                        icon = Icons.Default.Delete,
                        contentColor = MaterialTheme.colorScheme.extended.destructiveHover,
                        onClick = onDeleteClick
                    ),
                )
            )
        }

        if (message.deliveryStatus == ChatMessageDeliveryStatus.FAILED) {
            IconButton(
                onClick = onRetryClick
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.reload_icon),
                    contentDescription = stringResource(Res.string.retry),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
@Preview
fun CurrentUserMessagePreview() {
    KrossChatTheme {
        CurrentUserMessage(
            message = MessageUi.CurrentUserMessage(
                id = "1",
                content = "Hello world, this is a preview message that spans multiple lines",
                deliveryStatus = ChatMessageDeliveryStatus.SENT,
                formattedSentTime = UiText.DynamicString("Friday 2:20pm")
            ),
            onRetryClick = {},
            onMessageLongClick = {},
            onDismissMessageMenu = {},
            onDeleteClick = {},
            messageWithOpenMenu = null
            )
    }
}