package com.taoufikcode.core.designsystem.components.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp

@Composable
fun DynamicRoundedCornerColumn(
    isCornersRounded: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier.shadow(
            elevation = if (isCornersRounded) 4.dp else 0.dp,
            shape = if (isCornersRounded) RoundedCornerShape(16.dp) else RectangleShape
        ).background(
            color = MaterialTheme.colorScheme.surface,
            shape = if (isCornersRounded) RoundedCornerShape(16.dp) else RectangleShape
        )
    ) {
        content()
    }
}