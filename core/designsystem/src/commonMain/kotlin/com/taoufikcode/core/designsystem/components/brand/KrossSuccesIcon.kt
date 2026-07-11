package com.taoufikcode.core.designsystem.components.brand


import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.taoufikcode.core.designsystem.theme.extended
import krosschat.core.designsystem.generated.resources.Res
import krosschat.core.designsystem.generated.resources.success_checkmark
import org.jetbrains.compose.resources.vectorResource
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun KrossSuccessIcon(
    modifier: Modifier = Modifier
) {
    Icon(
        imageVector = vectorResource(Res.drawable.success_checkmark),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.extended.success,
        modifier = modifier
    )
}
@Composable
@Preview
fun KrossSuccessIconPreview() {
    KrossSuccessIcon()
}