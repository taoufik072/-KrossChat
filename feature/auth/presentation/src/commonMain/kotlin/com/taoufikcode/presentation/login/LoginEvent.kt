package com.taoufikcode.presentation.login

sealed interface LoginEvent {
    data object Success: LoginEvent
}