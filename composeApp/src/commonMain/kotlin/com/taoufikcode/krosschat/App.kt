package com.taoufikcode.krosschat

import androidx.compose.runtime.Composable
import com.taoufikcode.core.designsystem.theme.KrossChatTheme
import com.taoufikcode.presentation.register.RegisterRoot
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    KrossChatTheme {
        RegisterRoot(
            onRegisterSuccess = {  }
        )
    }
}