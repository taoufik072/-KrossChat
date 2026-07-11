package com.taoufikcode.core.designsystem.components.brand


import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.taoufikcode.core.designsystem.theme.KrossChatTheme
import krosschat.core.designsystem.generated.resources.Res
import krosschat.core.designsystem.generated.resources.logo_kross
import org.jetbrains.compose.resources.vectorResource
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun KrossBrandLogo(
    modifier: Modifier = Modifier
) {
    Icon(
        imageVector = vectorResource(Res.drawable.logo_kross),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
        modifier = modifier.size(48.dp)
    )
}

@Composable
@Preview
fun KrossBrandLogoPre(

) {
    KrossChatTheme {
        KrossBrandLogo()
    }
}