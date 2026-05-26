package com.taoufikcode.core.data.dto.requests

import kotlinx.serialization.Serializable

@Serializable
class RegisterRequest(
    val email: String,
    val username: String,
    val password: String
)