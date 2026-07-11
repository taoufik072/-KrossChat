package com.taoufikcode.presentation.register_success

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.taoufikcode.core.designsystem.components.brand.KrossSuccessIcon
import com.taoufikcode.core.designsystem.components.buttons.KrossButton
import com.taoufikcode.core.designsystem.components.buttons.KrossButtonStyle
import com.taoufikcode.core.designsystem.components.layouts.KrossAdaptiveResultLayout
import com.taoufikcode.core.designsystem.components.layouts.KrossSimpleResultLayout
import com.taoufikcode.core.designsystem.components.layouts.KrossSnackBarScaffold
import com.taoufikcode.core.designsystem.theme.KrossChatTheme
import com.taoufikcode.core.presentation.utils.ObserveAsEvents
import com.taoufikcode.presentation.register.RegisterAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import krosschat.feature.auth.presentation.generated.resources.Res
import krosschat.feature.auth.presentation.generated.resources.account_successfully_created
import krosschat.feature.auth.presentation.generated.resources.login
import krosschat.feature.auth.presentation.generated.resources.resend_verification_email
import krosschat.feature.auth.presentation.generated.resources.resent_verification_email
import krosschat.feature.auth.presentation.generated.resources.verification_email_sent_to_x
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RegisterSuccessRoot(
    viewModel: RegisterSuccessViewModel = koinViewModel(),
    onLoginClick: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is RegisterSuccessEvent.ResendVerificationEmailSuccess -> {
                snackBarHostState.showSnackbar(
                    message = getString(
                        resource = Res.string.resent_verification_email
                    )
                )
            }
        }
    }
    RegisterSuccessScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is RegisterSuccessAction.OnLoginClick -> onLoginClick()
                else -> Unit
            }
            viewModel.onAction(action)
        },
        snackBarHostState = snackBarHostState
    )
}

@Composable
fun RegisterSuccessScreen(
    state: RegisterSuccessState,
    onAction: (RegisterSuccessAction) -> Unit,
    snackBarHostState: SnackbarHostState,
) {
    KrossSnackBarScaffold(
        snackBarHostState = snackBarHostState
    ) {
        KrossAdaptiveResultLayout {
            KrossSimpleResultLayout(
                title = stringResource(Res.string.account_successfully_created),
                description = stringResource(
                    Res.string.verification_email_sent_to_x,
                    state.registeredEmail
                ),
                icon = { KrossSuccessIcon() },
                primaryButton = {
                    KrossButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(Res.string.login),
                        onClick = { onAction(RegisterSuccessAction.OnLoginClick) }
                    )
                },
                secondaryButton = {
                    KrossButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(Res.string.resend_verification_email),
                        onClick = { onAction(RegisterSuccessAction.OnResendVerificationEmail) },
                        enabled = !state.isResendingVerificationEmail,
                        isLoading = state.isResendingVerificationEmail,
                        style = KrossButtonStyle.SECONDARY
                    )
                }
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    KrossChatTheme {
        RegisterSuccessScreen(
            state = RegisterSuccessState(
                registeredEmail = "james.a.garfield@examplepetstore.com",
                isResendingVerificationEmail = false
            ),
            onAction = {},
            snackBarHostState = remember { SnackbarHostState() }
        )
    }
}