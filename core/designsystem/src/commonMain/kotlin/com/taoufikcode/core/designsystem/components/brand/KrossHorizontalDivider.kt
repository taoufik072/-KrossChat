package com.taoufikcode.core.designsystem.components.brand

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.taoufikcode.core.designsystem.theme.KrossChatTheme
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun KrossHorizontalDivider(
    modifier: Modifier = Modifier
) {
    HorizontalDivider(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.outline
    )
}
@Composable
@Preview
fun KrossHorizontalDividerPreview() {
    KrossChatTheme {
        KrossHorizontalDivider(
            modifier = Modifier.height(20.dp)
        )
    }
}