package com.taoufikcode.chat.presentation.chat_detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.taoufikcode.chat.domain.models.ChatMessageDeliveryStatus
import com.taoufikcode.core.designsystem.theme.KrossChatTheme
import com.taoufikcode.core.designsystem.theme.extended
import com.taoufikcode.core.designsystem.theme.labelXSmall
import krosschat.feature.chat.presentation.generated.resources.Res
import krosschat.feature.chat.presentation.generated.resources.check_icon
import krosschat.feature.chat.presentation.generated.resources.failed
import krosschat.feature.chat.presentation.generated.resources.loading_icon
import krosschat.feature.chat.presentation.generated.resources.sending
import krosschat.feature.chat.presentation.generated.resources.sent
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun MessageStatus(
    status: ChatMessageDeliveryStatus,
    modifier: Modifier = Modifier
) {
    val (text, icon, color) = when (status) {
        ChatMessageDeliveryStatus.SENDING -> Triple(
            stringResource(Res.string.sending),
            vectorResource(Res.drawable.loading_icon),
            MaterialTheme.colorScheme.extended.textDisabled
        )

        ChatMessageDeliveryStatus.SENT -> Triple(
            stringResource(Res.string.sent),
            vectorResource(Res.drawable.check_icon),
            MaterialTheme.colorScheme.extended.textTertiary
        )

        ChatMessageDeliveryStatus.FAILED -> Triple(
            stringResource(Res.string.failed),
            Icons.Default.Close,
            MaterialTheme.colorScheme.error
        )
    }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = color,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            color = color,
            style = MaterialTheme.typography.labelXSmall
        )
    }
}

@Composable
@Preview
fun MessageStatusPreview() {
    KrossChatTheme {
        Column(
            modifier = Modifier.wrapContentSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MessageStatus(
                status = ChatMessageDeliveryStatus.SENT
            )
            MessageStatus(
                status = ChatMessageDeliveryStatus.SENDING
            )
            MessageStatus(
                status = ChatMessageDeliveryStatus.FAILED
            )
        }
    }
}
