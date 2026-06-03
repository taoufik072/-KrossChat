package com.taoufikcode.chat.presentation.mappers

import com.taoufikcode.chat.domain.models.ConnectionState
import com.taoufikcode.core.presentation.utils.UiText
import krosschat.feature.chat.presentation.generated.resources.Res
import krosschat.feature.chat.presentation.generated.resources.network_error
import krosschat.feature.chat.presentation.generated.resources.offline
import krosschat.feature.chat.presentation.generated.resources.online
import krosschat.feature.chat.presentation.generated.resources.reconnecting
import krosschat.feature.chat.presentation.generated.resources.unknown_error

fun ConnectionState.toUiText(): UiText {
    val resource = when (this) {
        ConnectionState.DISCONNECTED -> Res.string.offline
        ConnectionState.CONNECTING -> Res.string.reconnecting
        ConnectionState.CONNECTED -> Res.string.online
        ConnectionState.ERROR_NETWORK -> Res.string.network_error
        ConnectionState.ERROR_UNKNOWN -> Res.string.unknown_error
    }
    return UiText.Resource(resource)
}