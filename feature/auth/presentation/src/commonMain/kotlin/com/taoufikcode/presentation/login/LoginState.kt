package com.taoufikcode.presentation.login

import androidx.compose.foundation.text.input.TextFieldState
import com.taoufikcode.core.presentation.utils.UiText

data class LoginState(
    val emailTextFieldState: TextFieldState = TextFieldState(),
    val passwordTextFieldState: TextFieldState = TextFieldState(),
    val isPasswordVisible: Boolean = false,
    val canLogin: Boolean = false,
    val isLoggingIn: Boolean = false,
    val error: UiText? = null
)