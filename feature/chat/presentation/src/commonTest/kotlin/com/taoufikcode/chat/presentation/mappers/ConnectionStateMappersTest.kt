package com.taoufikcode.chat.presentation.mappers

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.taoufikcode.chat.domain.models.ConnectionState
import com.taoufikcode.core.presentation.utils.UiText
import krosschat.feature.chat.presentation.generated.resources.Res
import krosschat.feature.chat.presentation.generated.resources.network_error
import krosschat.feature.chat.presentation.generated.resources.offline
import krosschat.feature.chat.presentation.generated.resources.online
import krosschat.feature.chat.presentation.generated.resources.reconnecting
import krosschat.feature.chat.presentation.generated.resources.unknown_error
import kotlin.test.Test

class ConnectionStateMappersTest {

    @Test
    fun `every connection state maps to its string resource`() {
        val expectedResources = mapOf(
            ConnectionState.DISCONNECTED to Res.string.offline,
            ConnectionState.CONNECTING to Res.string.reconnecting,
            ConnectionState.CONNECTED to Res.string.online,
            ConnectionState.ERROR_NETWORK to Res.string.network_error,
            ConnectionState.ERROR_UNKNOWN to Res.string.unknown_error
        )

        ConnectionState.entries.forEach { state ->
            val uiText = state.toUiText()

            assertThat((uiText as UiText.Resource).id, name = state.name)
                .isEqualTo(expectedResources.getValue(state))
        }
    }
}
