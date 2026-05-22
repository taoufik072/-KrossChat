package com.taoufikcode.core.designsystem.components.brand


import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import krosschat.core.designsystem.generated.resources.Res
import krosschat.core.designsystem.generated.resources.logo_kross
import org.jetbrains.compose.resources.vectorResource

@Composable
fun KrossBrandLogo(
    modifier: Modifier = Modifier
) {
    Icon(
        imageVector = vectorResource(Res.drawable.logo_kross),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
        modifier = modifier
    )
}