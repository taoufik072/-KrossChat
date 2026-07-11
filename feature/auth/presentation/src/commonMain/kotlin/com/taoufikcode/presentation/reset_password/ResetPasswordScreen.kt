package com.taoufikcode.presentation.reset_password

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.taoufikcode.core.designsystem.components.brand.KrossBrandLogo
import com.taoufikcode.core.designsystem.components.buttons.KrossButton
import com.taoufikcode.core.designsystem.components.layouts.KrossAdaptiveFormLayout
import com.taoufikcode.core.designsystem.components.layouts.KrossSnackBarScaffold
import com.taoufikcode.core.designsystem.components.textfields.KrossPasswordTextField
import com.taoufikcode.core.designsystem.theme.KrossChatTheme
import com.taoufikcode.core.designsystem.theme.extended
import krosschat.feature.auth.presentation.generated.resources.Res
import krosschat.feature.auth.presentation.generated.resources.password
import krosschat.feature.auth.presentation.generated.resources.password_hint
import krosschat.feature.auth.presentation.generated.resources.reset_password_successfully
import krosschat.feature.auth.presentation.generated.resources.set_new_password
import krosschat.feature.auth.presentation.generated.resources.submit
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ResetPasswordRoot(
    viewModel: ResetPasswordViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ResetPasswordScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun ResetPasswordScreen(
    state: ResetPasswordState,
    onAction: (ResetPasswordAction) -> Unit,
) {
    KrossSnackBarScaffold {
        KrossAdaptiveFormLayout(
            headerText = stringResource(Res.string.set_new_password),
            errorText = state.errorText?.asString(),
            logo = {
                KrossBrandLogo()
            }
        ) {
            KrossPasswordTextField(
                state = state.passwordTextState,
                modifier = Modifier
                    .fillMaxWidth(),
                placeholder = stringResource(Res.string.password),
                title = stringResource(Res.string.password),
                supportingText = stringResource(Res.string.password_hint),
                isPasswordVisible = state.isPasswordVisible,
                onToggleVisibilityClick = {
                    onAction(ResetPasswordAction.OnTogglePasswordVisibilityClick)
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            KrossButton(
                text = stringResource(Res.string.submit),
                onClick = {
                    onAction(ResetPasswordAction.OnSubmitClick)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading && state.canSubmit,
                isLoading = state.isLoading
            )
            if (state.isResetSuccessful) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(Res.string.reset_password_successfully),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.extended.success,
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    KrossChatTheme {
        ResetPasswordScreen(
            state = ResetPasswordState(),
            onAction = {}
        )
    }
}