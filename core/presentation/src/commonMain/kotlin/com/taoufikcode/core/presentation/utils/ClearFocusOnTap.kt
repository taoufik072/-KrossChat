package com.taoufikcode.core.presentation.utils

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import com.taoufikcode.core.presentation.isIos

@Composable
fun Modifier.clearFocusOnTap(): Modifier {
    val focusManager = LocalFocusManager.current
    return if (isIos) {
        this.pointerInput(Unit) {
            detectTapGestures {
                focusManager.clearFocus()
            }
        }
    } else {
        this
    }
}