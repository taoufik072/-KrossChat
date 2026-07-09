package com.taoufikcode.core.domain.notification

import com.taoufikcode.core.domain.util.DataError
import com.taoufikcode.core.domain.util.EmptyResult

interface DeviceTokenService {

    suspend fun registerToken(
        token: String,
        platform: String
    ): EmptyResult<DataError.Remote>

    suspend fun unregisterToken(
        token: String
    ): EmptyResult<DataError.Remote>
}