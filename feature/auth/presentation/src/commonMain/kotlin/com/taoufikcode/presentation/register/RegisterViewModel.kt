package com.taoufikcode.presentation.register

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taoufikcode.core.domain.auth.AuthService
import com.taoufikcode.core.domain.util.DataError
import com.taoufikcode.core.domain.util.onFailure
import com.taoufikcode.core.domain.util.onSuccess
import com.taoufikcode.core.domain.validation.PasswordValidator
import com.taoufikcode.core.presentation.mapper.toUiText
import com.taoufikcode.core.presentation.utils.UiText
import com.taoufikcode.domain.EmailValidator
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import krosschat.feature.auth.presentation.generated.resources.Res
import krosschat.feature.auth.presentation.generated.resources.error_account_exists
import krosschat.feature.auth.presentation.generated.resources.error_invalid_email
import krosschat.feature.auth.presentation.generated.resources.error_invalid_password
import krosschat.feature.auth.presentation.generated.resources.error_invalid_username

class RegisterViewModel(
    private val authService: AuthService
) : ViewModel() {

    private var hasLoadedInitialData = false
    private val _state = MutableStateFlow(RegisterState())
    private val _events = Channel<RegisterEvent>()
    val events = _events.receiveAsFlow()
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                observeValidationState()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = RegisterState()
        )
    private val isEmailValidFlow = snapshotFlow { _state.value.emailTextState.text.toString() }
        .map { email -> EmailValidator.validate(email) }
        .distinctUntilChanged()
    private val isUsernameValidFlow = snapshotFlow { _state.value.usernameTextState.text.toString() }
        .map { username -> username.length in 3..20 }
        .distinctUntilChanged()
    private val isPasswordValidFlow = snapshotFlow { _state.value.passwordTextState.text.toString() }
        .map { password -> PasswordValidator.validate(password).isValidPassword }
        .distinctUntilChanged()

    val isRegisteringFlow = state.map { it.isRegistering }.distinctUntilChanged()

    private fun observeValidationState() {
        combine(
            isEmailValidFlow,
            isUsernameValidFlow,
            isPasswordValidFlow,
            isRegisteringFlow
        ) { emailIsValid,
            usernameIsValid,
            passwordIsValid,
            isRegistering ->

            val allValid = emailIsValid && usernameIsValid && passwordIsValid
            _state.update {
                it.copy(
                    canRegister = !isRegistering && allValid
                )
            }
        }.launchIn(viewModelScope)
    }

    fun onAction(action: RegisterAction) {
        when (action) {
            RegisterAction.OnLoginClick -> validateFormInputs()
            RegisterAction.OnRegisterClick -> register()
            RegisterAction.OnTogglePasswordVisibilityClick -> {
                _state.update {
                    it.copy(
                        isPasswordVisible = !it.isPasswordVisible
                    )
                }
            }

            else -> Unit
        }
    }

    private fun clearAllTextFieldErrors() {
        _state.update {
            it.copy(
                emailError = null,
                usernameError = null,
                passwordError = null,
                registrationError = null
            )
        }
    }

    private fun validateFormInputs(): Boolean {
        clearAllTextFieldErrors()

        val currentState = state.value
        val email = currentState.emailTextState.text.toString()
        val username = currentState.usernameTextState.text.toString()
        val password = currentState.passwordTextState.text.toString()

        val isEmailValid = EmailValidator.validate(email)
        val passwordValidationState = PasswordValidator.validate(password)
        val isUsernameValid = username.length in 3..20

        val emailError = if (!isEmailValid) {
            UiText.Resource(Res.string.error_invalid_email)
        } else null
        val usernameError = if (!isUsernameValid) {
            UiText.Resource(Res.string.error_invalid_username)
        } else null
        val passwordError = if (!passwordValidationState.isValidPassword) {
            UiText.Resource(Res.string.error_invalid_password)
        } else null

        _state.update {
            it.copy(
                emailError = emailError,
                usernameError = usernameError,
                passwordError = passwordError
            )
        }

        return isUsernameValid && isEmailValid && passwordValidationState.isValidPassword
    }

    fun register() {
        if (!validateFormInputs()) {
            return
        }
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isRegistering = true
                )
            }
            val email = state.value.emailTextState.text.toString()
            val userName = state.value.usernameTextState.text.toString()
            val password = state.value.passwordTextState.text.toString()

            authService.register(
                email = email,
                username = userName,
                password = password
            ).onSuccess {
                _state.update {
                    it.copy(
                        isRegistering = false
                    )
                }
                _events.send(RegisterEvent.Success(email = email))
            }.onFailure { error ->
                val registrationError = when(error) {
                    DataError.Remote.CONFLICT -> UiText.Resource(Res.string.error_account_exists)
                    else -> error.toUiText()
                }
                _state.update { it.copy(
                    isRegistering = false,
                    registrationError = registrationError,
                ) }
            }
        }
    }

}