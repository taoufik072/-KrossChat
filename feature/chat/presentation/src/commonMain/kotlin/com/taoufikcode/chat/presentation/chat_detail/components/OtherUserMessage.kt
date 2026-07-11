package com.taoufikcode.chat.presentation.chat_detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.taoufikcode.chat.presentation.model.MessageUi
import com.taoufikcode.chat.presentation.util.getChatBubbleColorForUser
import com.taoufikcode.core.designsystem.components.avatar.ChatParticipantUi
import com.taoufikcode.core.designsystem.components.avatar.KrossAvatarPhoto
import com.taoufikcode.core.designsystem.components.chat.KrossChatBubble
import com.taoufikcode.core.designsystem.components.chat.TrianglePosition
import com.taoufikcode.core.designsystem.theme.KrossChatTheme
import com.taoufikcode.core.presentation.utils.UiText
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun OtherUserMessage(
    modifier: Modifier = Modifier,
    message: MessageUi.OtherUserMessage,
    color: Color,
    ) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        KrossAvatarPhoto(
            displayText = message.sender.initials, imageUrl = message.sender.imageUrl
        )
        KrossChatBubble(
            messageContent = message.content,
            sender = message.sender.username,
            color = color,
            trianglePosition = TrianglePosition.LEFT,
            formattedDateTime = message.formattedSentTime.asString()
        )
    }
}

@Composable
@Preview
fun OtherUserMessagePreview() {
    KrossChatTheme {
        OtherUserMessage(
            message = MessageUi.OtherUserMessage(
                id = "1",
                content = "Hello world, this is a preview message that spans multiple lines",
                formattedSentTime = UiText.DynamicString("Friday 2:20pm"),
                sender = ChatParticipantUi(
                    id = "1",
                    username = "Taoufik",
                    initials = "TA"
                ),
            ),
            color = getChatBubbleColorForUser("1")
        )
    }
}
