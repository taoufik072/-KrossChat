package com.taoufikcode.core.designsystem.components.avatar

import androidx.compose.runtime.Immutable

@Immutable
data class ChatParticipantUi(
    val id: String,
    val username: String,
    val initials: String,
    val imageUrl: String? = null
)
