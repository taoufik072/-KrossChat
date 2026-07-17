package com.taoufikcode.core.presentation.mapper

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.taoufikcode.core.domain.util.DataError
import com.taoufikcode.core.presentation.utils.UiText
import krosschat.core.presentation.generated.resources.Res.string
import krosschat.core.presentation.generated.resources.error_bad_request
import krosschat.core.presentation.generated.resources.error_conflict
import krosschat.core.presentation.generated.resources.error_disk_full
import krosschat.core.presentation.generated.resources.error_forbidden
import krosschat.core.presentation.generated.resources.error_no_internet
import krosschat.core.presentation.generated.resources.error_not_found
import krosschat.core.presentation.generated.resources.error_payload_too_large
import krosschat.core.presentation.generated.resources.error_request_timeout
import krosschat.core.presentation.generated.resources.error_serialization
import krosschat.core.presentation.generated.resources.error_server
import krosschat.core.presentation.generated.resources.error_service_unavailable
import krosschat.core.presentation.generated.resources.error_too_many_requests
import krosschat.core.presentation.generated.resources.error_unable_to_send_message
import krosschat.core.presentation.generated.resources.error_unauthorized
import krosschat.core.presentation.generated.resources.error_unknown
import kotlin.test.Test

class DataErrorToUiTest {

    @Test
    fun `every DataError maps to its string resource`() {
        val expectedResources = mapOf(
            DataError.Local.DISK_FULL to string.error_disk_full,
            DataError.Local.NOT_FOUND to string.error_not_found,
            DataError.Local.UNKNOWN to string.error_unknown,
            DataError.Remote.BAD_REQUEST to string.error_bad_request,
            DataError.Remote.REQUEST_TIMEOUT to string.error_request_timeout,
            DataError.Remote.UNAUTHORIZED to string.error_unauthorized,
            DataError.Remote.FORBIDDEN to string.error_forbidden,
            DataError.Remote.NOT_FOUND to string.error_not_found,
            DataError.Remote.CONFLICT to string.error_conflict,
            DataError.Remote.TOO_MANY_REQUESTS to string.error_too_many_requests,
            DataError.Remote.NO_INTERNET to string.error_no_internet,
            DataError.Remote.PAYLOAD_TOO_LARGE to string.error_payload_too_large,
            DataError.Remote.SERVER_ERROR to string.error_server,
            DataError.Remote.SERVICE_UNAVAILABLE to string.error_service_unavailable,
            DataError.Remote.SERIALIZATION to string.error_serialization,
            DataError.Remote.UNKNOWN to string.error_unknown,
            DataError.Connection.NOT_CONNECTED to string.error_no_internet,
            DataError.Connection.MESSAGE_SEND_FAILED to string.error_unable_to_send_message
        )

        expectedResources.forEach { (error, expectedResource) ->
            val uiText = error.toUiText()

            assertThat(
                (uiText as UiText.Resource).id,
                name = error.toString()
            ).isEqualTo(expectedResource)
        }
    }

    @Test
    fun `all DataError values are covered by the mapping`() {
        val allErrors: List<DataError> =
            DataError.Remote.entries + DataError.Local.entries + DataError.Connection.entries

        allErrors.forEach { error ->
            error.toUiText()
        }
    }
}
