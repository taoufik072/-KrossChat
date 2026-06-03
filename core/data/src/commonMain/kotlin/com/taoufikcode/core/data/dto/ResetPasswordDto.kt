package com.taoufikcode.core.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ResetPasswordDto(
    val newPassword: String,
    val token: String
)
