package com.taoufikcode.core.presentation.mapper

import com.taoufikcode.core.domain.util.DataError
import com.taoufikcode.core.presentation.utils.UiText
import krosschat.core.presentation.generated.resources.Res
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

fun DataError.toUiText(): UiText {
    val resource = when(this) {
        DataError.Local.DISK_FULL -> string.error_disk_full
        DataError.Local.NOT_FOUND -> string.error_not_found
        DataError.Local.UNKNOWN -> string.error_unknown
        DataError.Remote.BAD_REQUEST -> string.error_bad_request
        DataError.Remote.REQUEST_TIMEOUT -> string.error_request_timeout
        DataError.Remote.UNAUTHORIZED -> string.error_unauthorized
        DataError.Remote.FORBIDDEN -> string.error_forbidden
        DataError.Remote.NOT_FOUND -> string.error_not_found
        DataError.Remote.CONFLICT -> string.error_conflict
        DataError.Remote.TOO_MANY_REQUESTS -> string.error_too_many_requests
        DataError.Remote.NO_INTERNET -> string.error_no_internet
        DataError.Remote.PAYLOAD_TOO_LARGE -> string.error_payload_too_large
        DataError.Remote.SERVER_ERROR -> string.error_server
        DataError.Remote.SERVICE_UNAVAILABLE -> string.error_service_unavailable
        DataError.Remote.SERIALIZATION -> string.error_serialization
        DataError.Remote.UNKNOWN -> string.error_unknown
        DataError.Connection.NOT_CONNECTED -> string.error_no_internet
        DataError.Connection.MESSAGE_SEND_FAILED -> string.error_unable_to_send_message

    }
    return UiText.Resource(resource)
}