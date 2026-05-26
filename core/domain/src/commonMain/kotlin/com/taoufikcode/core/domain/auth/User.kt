package com.taoufikcode.core.domain.auth

data class User(
    val id: String,
    val email: String,
    val userName: String,
    val hasVerifiedEmail: Boolean,
    val profilePictureUrl: String?= null,
)
