package com.taoufikcode.core.designsystem.components.dialogs

import androidx.compose.runtime.Composable
import com.taoufikcode.core.presentation.utils.currentDeviceConfiguration

@Composable
fun KrossAdaptiveDialogSheetLayout(
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    val configuration = currentDeviceConfiguration()
    if(configuration.isMobile) {
        KrossBottomSheet(
            onDismiss = onDismiss,
            content = content
        )
    } else {
        KrossDialogContent(
            onDismiss = onDismiss,
            content = content
        )
    }
}