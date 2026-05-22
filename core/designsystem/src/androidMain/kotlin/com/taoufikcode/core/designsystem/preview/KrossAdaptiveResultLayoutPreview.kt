package com.taoufikcode.core.designsystem.preview

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.taoufikcode.core.designsystem.components.layouts.KrossAdaptiveResultLayout
import com.taoufikcode.core.designsystem.theme.KrossChatTheme

@Composable
@PreviewLightDark
@PreviewScreenSizes
fun KrossAdaptiveResultLayoutPreview() {
    KrossChatTheme {
        KrossAdaptiveResultLayout(
            modifier = Modifier
                .fillMaxSize(),
            content = {
                Text(
                    text = "Registration successful!",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        )
    }
}