package com.taoufikcode.presentation.login


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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
import krosschat.feature.auth.presentation.generated.resources.create_account
import krosschat.feature.auth.presentation.generated.resources.email
import krosschat.feature.auth.presentation.generated.resources.email_placeholder
import krosschat.feature.auth.presentation.generated.resources.forgot_password
import krosschat.feature.auth.presentation.generated.resources.login
import krosschat.feature.auth.presentation.generated.resources.password
import krosschat.feature.auth.presentation.generated.resources.welcome_back
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginRoot(
    viewModel: LoginViewModel = koinViewModel(),
    onLoginSuccess: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onCreateAccountClick: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            LoginEvent.Success -> onLoginSuccess()
        }
    }

    LoginScreen(
        state = state,
        onAction = { action ->
            when (action) {
                LoginAction.OnForgotPasswordClick -> onForgotPasswordClick()
                LoginAction.OnSignUpClick -> onCreateAccountClick()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
fun LoginScreen(
    state: LoginState,
    onAction: (LoginAction) -> Unit,
) {
    KrossSnackBarScaffold {
        KrossAdaptiveFormLayout(
            headerText = stringResource(Res.string.welcome_back),
            errorText = state.error?.asString(),
            logo = {
                KrossBrandLogo()
            },
            modifier = Modifier
                .fillMaxSize()
        ) {
            KrossTextField(
                state = state.emailTextFieldState,
                placeholder = stringResource(Res.string.email_placeholder),
                keyboardType = KeyboardType.Email,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth(),
                title = stringResource(Res.string.email)
            )
            Spacer(modifier = Modifier.height(16.dp))
            KrossPasswordTextField(
                state = state.passwordTextFieldState,
                placeholder = stringResource(Res.string.password),
                isPasswordVisible = state.isPasswordVisible,
                onToggleVisibilityClick = {
                    onAction(LoginAction.OnTogglePasswordVisibility)
                },
                title = stringResource(Res.string.password),
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(Res.string.forgot_password),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable {
                        onAction(LoginAction.OnForgotPasswordClick)
                    }
            )
            Spacer(modifier = Modifier.height(24.dp))

            KrossButton(
                text = stringResource(Res.string.login),
                onClick = {
                    onAction(LoginAction.OnLoginClick)
                },
                enabled = state.canLogin,
                isLoading = state.isLoggingIn,
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            KrossButton(
                text = stringResource(Res.string.create_account),
                onClick = {
                    onAction(LoginAction.OnSignUpClick)
                },
                style = KrossButtonStyle.SECONDARY,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
private fun LightThemePreview() {
    KrossChatTheme {
        LoginScreen(
            state = LoginState(),
            onAction = {}
        )
    }
}

@Preview
@Composable
private fun DarkThemePreview() {
    KrossChatTheme(darkTheme = true) {
        LoginScreen(
            state = LoginState(),
            onAction = {}
        )
    }
}