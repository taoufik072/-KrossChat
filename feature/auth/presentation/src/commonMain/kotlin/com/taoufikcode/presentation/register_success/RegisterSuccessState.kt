package com.taoufikcode.presentation.register_success

import com.taoufikcode.core.presentation.utils.UiText

data class RegisterSuccessState(
    val registeredEmail: String = "",
    val isResendingVerificationEmail: Boolean = false,
    val resendVerificationError: UiText? = null
)