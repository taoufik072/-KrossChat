package com.taoufikcode.core.designsystem.components.badge

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.taoufikcode.core.designsystem.theme.KrossChatTheme
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun KrossCountBadge(
    count: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .defaultMinSize(minWidth = 20.dp, minHeight = 20.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (count > 99) "99+" else count.toString(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
@Preview
fun KrossCountBadgePreview() {
    KrossChatTheme {
        KrossCountBadge(count = 3)
    }
}

@Composable
@Preview
fun KrossCountBadgeOverflowPreview() {
    KrossChatTheme {
        KrossCountBadge(count = 128)
    }
}

@Composable
@Preview
fun KrossCountBadgeDarkThemePreview() {
    KrossChatTheme(darkTheme = true) {
        KrossCountBadge(count = 5)
    }
}
