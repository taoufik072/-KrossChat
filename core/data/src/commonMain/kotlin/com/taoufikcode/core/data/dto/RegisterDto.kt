package com.taoufikcode.core.data.dto

import kotlinx.serialization.Serializable

@Serializable
class RegisterDto(
    val email: String,
    val username: String,
    val password: String
)