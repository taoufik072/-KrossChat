package com.taoufikcode.chat.presentation.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.taoufikcode.core.designsystem.components.buttons.KrossButton
import com.taoufikcode.core.designsystem.components.textfields.KrossTextField
import com.taoufikcode.core.designsystem.theme.KrossChatTheme
import com.taoufikcode.core.designsystem.theme.extended
import com.taoufikcode.core.presentation.utils.DeviceConfiguration
import com.taoufikcode.core.presentation.utils.currentDeviceConfiguration
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ProfileSectionLayout(
    headerText: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val deviceConfiguration = currentDeviceConfiguration()
    when (deviceConfiguration) {
        DeviceConfiguration.MOBILE_PORTRAIT -> {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.Start,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = headerText,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.extended.textTertiary
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    content()
                }
            }
        }

        else -> {
            Row(
                verticalAlignment = Alignment.Top,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = headerText,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.extended.textTertiary,
                    modifier = Modifier.weight(1f)
                )
                Column(
                    modifier = Modifier
                        .weight(3f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    content()
                }
            }
        }
    }
}

@Preview
@Composable
private fun ProfileSectionLayoutPreview() {
    KrossChatTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
        ) {
            ProfileSectionLayout(
                headerText = "Email"
            ) {
                KrossTextField(
                    state = rememberTextFieldState(),
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = "example@gmail.com",
                    title = null
                )
                KrossButton(
                    modifier = Modifier.align(Alignment.End),
                    text = "Save",
                    onClick = {}
                )
            }
        }
    }
}

