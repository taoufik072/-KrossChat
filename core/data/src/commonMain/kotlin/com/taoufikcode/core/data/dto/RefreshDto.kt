package com.taoufikcode.core.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class RefreshDto(
    val refreshToken: String
)