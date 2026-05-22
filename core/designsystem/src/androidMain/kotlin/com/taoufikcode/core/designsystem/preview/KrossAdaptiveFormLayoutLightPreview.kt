package com.taoufikcode.core.designsystem.preview

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.taoufikcode.core.designsystem.components.brand.KrossBrandLogo
import com.taoufikcode.core.designsystem.components.layouts.KrossAdaptiveFormLayout
import com.taoufikcode.core.designsystem.theme.KrossChatTheme

@Composable
@PreviewLightDark
@PreviewScreenSizes
fun KrossAdaptiveFormLayoutLightPreview() {
    KrossChatTheme {
        KrossAdaptiveFormLayout(
            headerText = "Welcome to Kross!",
            errorText = "Login failed!",
            logo = { KrossBrandLogo() },
            formContent = {
                Text(
                    text = "Sample form title",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Sample form title 2",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        )
    }
}