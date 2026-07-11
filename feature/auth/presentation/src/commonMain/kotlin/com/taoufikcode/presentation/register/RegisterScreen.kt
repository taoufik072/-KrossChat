package com.taoufikcode.presentation.register

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.taoufikcode.core.designsystem.components.brand.KrossBrandLogo
import com.taoufikcode.core.designsystem.components.buttons.KrossButton
import com.taoufikcode.core.designsystem.components.buttons.KrossButtonStyle
import com.taoufikcode.core.designsystem.components.layouts.KrossAdaptiveFormLayout
import com.taoufikcode.core.designsystem.components.layouts.KrossSnackBarScaffold
import com.taoufikcode.core.designsystem.components.textfields.KrossPasswordTextField
import com.taoufikcode.core.designsystem.components.textfields.KrossTextField
import com.taoufikcode.core.designsystem.theme.KrossChatTheme
import com.taoufikcode.core.presentation.utils.ObserveAsEvents
import krosschat.feature.auth.presentation.generated.resources.Res
import krosschat.feature.auth.presentation.generated.resources.email
import krosschat.feature.auth.presentation.generated.resources.email_placeholder
import krosschat.feature.auth.presentation.generated.resources.login
import krosschat.feature.auth.presentation.generated.resources.password
import krosschat.feature.auth.presentation.generated.resources.password_hint
import krosschat.feature.auth.presentation.generated.resources.register
import krosschat.feature.auth.presentation.generated.resources.username
import krosschat.feature.auth.presentation.generated.resources.username_hint
import krosschat.feature.auth.presentation.generated.resources.username_placeholder
import krosschat.feature.auth.presentation.generated.resources.welcome_to_kross
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RegisterRoot(
    viewModel: RegisterViewModel = koinViewModel(),
    onRegisterSuccess: (String) -> Unit,
    onLoginClick: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is RegisterEvent.Success -> {
                onRegisterSuccess(event.email)
            }
        }
    }

    RegisterScreen(
        state = state,
        onAction = { action ->
            when(action) {
                is RegisterAction.OnLoginClick -> onLoginClick()
                else -> Unit
            }
            viewModel.onAction(action)
        },
        snackbarHostState = snackbarHostState
    )
}

@Composable
fun RegisterScreen(
    state: RegisterState,
    onAction: (RegisterAction) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    KrossSnackBarScaffold(
        snackBarHostState = snackbarHostState
    ) {
        KrossAdaptiveFormLayout(
            headerText = stringResource(Res.string.welcome_to_kross),
            errorText = state.registrationError?.asString(),
            logo = { KrossBrandLogo() }
        ) {
            KrossTextField(
                state = state.usernameTextState,
                placeholder = stringResource(Res.string.username_placeholder),
                title = stringResource(Res.string.username),
                supportingText = state.usernameError?.asString()
                    ?: stringResource(Res.string.username_hint),
                isError = state.usernameError != null,
                onFocusChanged = { isFocused ->
                    onAction(RegisterAction.OnInputTextFocusGain)
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            KrossTextField(
                state = state.emailTextState,
                placeholder = stringResource(Res.string.email_placeholder),
                title = stringResource(Res.string.email),
                supportingText = state.emailError?.asString(),
                isError = state.emailError != null,
                keyboardType = KeyboardType.Email,
                onFocusChanged = { isFocused ->
                    onAction(RegisterAction.OnInputTextFocusGain)
                },

            )
            Spacer(modifier = Modifier.height(16.dp))
            KrossPasswordTextField(
                state = state.passwordTextState,
                placeholder = stringResource(Res.string.password),
                title = stringResource(Res.string.password),
                supportingText = state.passwordError?.asString()
                    ?: stringResource(Res.string.password_hint),
                isError = state.passwordError != null,
                onFocusChanged = { isFocused ->
                    onAction(RegisterAction.OnInputTextFocusGain)
                },
                onToggleVisibilityClick = {
                    onAction(RegisterAction.OnTogglePasswordVisibilityClick)
                },
                isPasswordVisible = state.isPasswordVisible
            )
            Spacer(modifier = Modifier.height(16.dp))

            KrossButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(Res.string.register),
                onClick = {
                    onAction(RegisterAction.OnRegisterClick)
                },
                enabled = state.canRegister,
                isLoading = state.isRegistering,

                )
            Spacer(modifier = Modifier.height(8.dp))
            KrossButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(Res.string.login),
                onClick = {
                    onAction(RegisterAction.OnLoginClick)
                },
                style = KrossButtonStyle.SECONDARY,

                )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    KrossChatTheme {
        RegisterScreen(
            state = RegisterState(),
            onAction = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}