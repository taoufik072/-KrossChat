package com.taoufikcode.chat.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ConfirmProfilePictureDto(
    val publicUrl: String
)