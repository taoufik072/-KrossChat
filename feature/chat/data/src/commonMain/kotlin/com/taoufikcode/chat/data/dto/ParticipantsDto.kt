package com.taoufikcode.chat.data.dto

import kotlinx.serialization.Serializable

@Serializable
class ParticipantsDto(
    val userIds: List<String>
)
