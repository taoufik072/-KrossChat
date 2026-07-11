package com.taoufikcode.chat.presentation.chat_detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.taoufikcode.chat.domain.models.ConnectionState
import com.taoufikcode.chat.presentation.mappers.toUiText
import com.taoufikcode.core.designsystem.components.buttons.KrossButton
import com.taoufikcode.core.designsystem.components.textfields.KrossMultiLineTextField
import com.taoufikcode.core.designsystem.theme.KrossChatTheme
import com.taoufikcode.core.designsystem.theme.extended
import krosschat.core.designsystem.generated.resources.cloud_off_icon
import krosschat.feature.chat.presentation.generated.resources.Res
import krosschat.feature.chat.presentation.generated.resources.send
import krosschat.feature.chat.presentation.generated.resources.send_a_message
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import krosschat.core.designsystem.generated.resources.Res as DesignSystemRes

@Composable
fun MessageBox(
    messageTextFieldState: TextFieldState,
    isSendButtonEnabled: Boolean,
    connectionState: ConnectionState,
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isConnected = connectionState == ConnectionState.CONNECTED
    // Optimized: get connection text once to avoid multiple calls to asString()
    val connectionText = connectionState.toUiText().asString()

    KrossMultiLineTextField(
        state = messageTextFieldState,
        modifier = modifier,
        placeholder = stringResource(Res.string.send_a_message),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Send
        ),
        onKeyboardAction = onSendClick,
        bottomContent = {
            Spacer(modifier = Modifier.weight(1f))
            if (!isConnected) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = vectorResource(DesignSystemRes.drawable.cloud_off_icon),
                        contentDescription = connectionText,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.extended.textDisabled
                    )
                    Text(
                        text = connectionText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.extended.textDisabled
                    )
                }
            }
            KrossButton(
                text = stringResource(Res.string.send),
                onClick = onSendClick,
                enabled = isConnected && isSendButtonEnabled
            )
        }
    )
}

@Composable
@Preview
fun MessageBoxPreview() {
    KrossChatTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            MessageBox(
                messageTextFieldState = rememberTextFieldState(),
                isSendButtonEnabled = true,
                connectionState = ConnectionState.CONNECTED,
                onSendClick = {},
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
@Preview
fun MessageBoxDisconnectedPreview() {
    KrossChatTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            MessageBox(
                messageTextFieldState = rememberTextFieldState(),
                isSendButtonEnabled = true,
                connectionState = ConnectionState.DISCONNECTED,
                onSendClick = {},
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
@Preview
fun MessageBoxConnectingPreview() {
    KrossChatTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            MessageBox(
                messageTextFieldState = rememberTextFieldState(),
                isSendButtonEnabled = true,
                connectionState = ConnectionState.CONNECTING,
                onSendClick = {},
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}