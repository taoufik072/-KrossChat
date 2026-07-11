package com.taoufikcode.core.designsystem.components.layouts


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.taoufikcode.core.designsystem.components.brand.KrossSuccessIcon
import com.taoufikcode.core.designsystem.components.buttons.KrossButton
import com.taoufikcode.core.designsystem.components.buttons.KrossButtonStyle
import com.taoufikcode.core.designsystem.theme.KrossChatTheme
import com.taoufikcode.core.designsystem.theme.extended
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun KrossSimpleResultLayout(
    title: String,
    description: String,
    icon: @Composable () -> Unit,
    primaryButton: @Composable () -> Unit,
    secondaryButton: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        icon()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = -(25).dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.extended.textPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.extended.textSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))

            primaryButton()

            if(secondaryButton != null) {
                Spacer(modifier = Modifier.height(8.dp))
                secondaryButton()
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
@Preview
fun KrossSimpleSuccessLayoutPreview() {
    KrossChatTheme(darkTheme = true) {
        KrossSimpleResultLayout(
            title = "Hello world!",
            description = "Test description",
            icon = {
                KrossSuccessIcon()
            },
            primaryButton = {
                KrossButton(
                    text = "Log In",
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                )
            },
            secondaryButton = {
                KrossButton(
                    text = "Resend verification email",
                    onClick = {},
                    style = KrossButtonStyle.SECONDARY,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        )
    }
}