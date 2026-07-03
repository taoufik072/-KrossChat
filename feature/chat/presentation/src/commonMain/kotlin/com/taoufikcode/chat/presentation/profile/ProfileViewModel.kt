package com.taoufikcode.chat.presentation.profile

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taoufikcode.chat.domain.repository.ProfileRepository
import com.taoufikcode.core.domain.auth.AuthService
import com.taoufikcode.core.domain.auth.SessionStorage
import com.taoufikcode.core.domain.util.DataError
import com.taoufikcode.core.domain.util.onFailure
import com.taoufikcode.core.domain.util.onSuccess
import com.taoufikcode.core.domain.validation.PasswordValidator
import com.taoufikcode.core.presentation.mapper.toUiText
import com.taoufikcode.core.presentation.utils.UiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import krosschat.feature.chat.presentation.generated.resources.Res
import krosschat.feature.chat.presentation.generated.resources.error_current_password_equal_to_new_one
import krosschat.feature.chat.presentation.generated.resources.error_current_password_incorrect
import krosschat.feature.chat.presentation.generated.resources.error_invalid_file_type

class ProfileViewModel(
    private val authService: AuthService,
    private val profileRepository: ProfileRepository,
    private val sessionStorage: SessionStorage
) : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(ProfileState())
    val state = combine(
        _state,
        sessionStorage.observeAuthInfo()
    ) { currentState, authInfo ->
        if (authInfo != null) {
            currentState.copy(
                username = authInfo.user.userName,
                userInitials = authInfo.user.userName.take(2),
                emailTextState = TextFieldState(initialText = authInfo.user.email),
                profilePictureUrl = authInfo.user.profilePictureUrl,
            )
        } else currentState
    }
        .onStart {
            if (!hasLoadedInitialData) {
                observeCanChangePassword()
                fetchCurrentUserDetails()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ProfileState()
        )

    fun onAction(action: ProfileAction) {
        when (action) {
            is ProfileAction.OnChangePasswordClick -> changePassword()
            is ProfileAction.OnToggleCurrentPasswordVisibility -> toggleCurrentPasswordVisibility()
            is ProfileAction.OnToggleNewPasswordVisibility -> toggleNewPasswordVisibility()
            is ProfileAction.OnPictureSelected -> uploadProfilePicture(
                action.bytes,
                action.mimeType
            )

            else -> Unit
        }
    }

    private fun uploadProfilePicture(bytes: ByteArray, mimeType: String?) {
        if (_state.value.isUploadingImage) {
            return
        }

        if (mimeType == null) {
            _state.update {
                it.copy(
                    imageError = UiText.Resource(Res.string.error_invalid_file_type)
                )
            }
            return
        }

        _state.update {
            it.copy(
                isUploadingImage = true,
                imageError = null
            )
        }

        viewModelScope.launch {
            profileRepository
                .uploadProfilePicture(
                    imageBytes = bytes,
                    mimeType = mimeType
                )
                .onSuccess {
                    _state.update {
                        it.copy(
                            isUploadingImage = false,
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            imageError = error.toUiText(),
                            isUploadingImage = false
                        )
                    }
                }
        }
    }

    private fun fetchCurrentUserDetails() {
        viewModelScope.launch {
            profileRepository.fetchCurrentUser()
        }
    }

    private fun toggleCurrentPasswordVisibility() {
        _state.update {
            it.copy(
                isCurrentPasswordVisible = !it.isCurrentPasswordVisible
            )
        }
    }

    private fun toggleNewPasswordVisibility() {
        _state.update {
            it.copy(
                isNewPasswordVisible = !it.isNewPasswordVisible
            )
        }
    }

    private fun observeCanChangePassword() {
        val isCurrentPasswordValidFlow = snapshotFlow {
            _state.value.currentPasswordTextState.text.toString()
        }.map { it.isNotBlank() }.distinctUntilChanged()

        val isNewPasswordValidFlow = snapshotFlow {
            _state.value.newPasswordTextState.text.toString()
        }.map {
            PasswordValidator.validate(it).isValidPassword
        }.distinctUntilChanged()

        combine(
            isCurrentPasswordValidFlow,
            isNewPasswordValidFlow
        ) { isCurrentValid, isNewValid ->
            _state.update {
                it.copy(
                    canChangePassword = isCurrentValid && isNewValid
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun changePassword() {
        if (!state.value.canChangePassword || state.value.isChangingPassword) {
            return
        }

        _state.update {
            it.copy(
                isChangingPassword = true,
                isPasswordChangeSuccessful = false
            )
        }
        viewModelScope.launch {
            val currentPassword = state.value.currentPasswordTextState.text.toString()
            val newPassword = state.value.newPasswordTextState.text.toString()
            authService
                .changePassword(
                    currentPassword = currentPassword,
                    newPassword = newPassword
                )
                .onSuccess {
                    state.value.currentPasswordTextState.clearText()
                    state.value.newPasswordTextState.clearText()

                    _state.update {
                        it.copy(
                            isChangingPassword = false,
                            newPasswordError = null,
                            isNewPasswordVisible = false,
                            isCurrentPasswordVisible = false,
                            isPasswordChangeSuccessful = true
                        )
                    }
                }
                .onFailure { error ->
                    val errorMessage = when (error) {
                        DataError.Remote.UNAUTHORIZED -> {
                            UiText.Resource(Res.string.error_current_password_incorrect)
                        }

                        DataError.Remote.CONFLICT -> {
                            UiText.Resource(Res.string.error_current_password_equal_to_new_one)
                        }

                        else -> error.toUiText()
                    }
                    _state.update {
                        it.copy(
                            newPasswordError = errorMessage,
                            isChangingPassword = false
                        )
                    }
                }
        }
    }

}