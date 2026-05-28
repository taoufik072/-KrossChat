package com.taoufikcode.krosschat

data class MainState(
    val isLoggedIn: Boolean = false,
    val isCheckingAuth: Boolean = true
)