package com.taoufikcode.core.data.notification

import com.taoufikcode.core.data.dto.RegisterDeviceTokenDto
import com.taoufikcode.core.data.network.delete
import com.taoufikcode.core.data.network.post
import com.taoufikcode.core.domain.notification.DeviceTokenService
import com.taoufikcode.core.domain.util.DataError
import com.taoufikcode.core.domain.util.EmptyResult
import io.ktor.client.HttpClient

class KtorDeviceTokenService(
    private val httpClient: HttpClient
) : DeviceTokenService {

    override suspend fun registerToken(
        token: String,
        platform: String
    ): EmptyResult<DataError.Remote> {
        return httpClient.post(
            route = "/notification/register",
            body = RegisterDeviceTokenDto(
                token = token,
                platform = platform
            )
        )
    }

    override suspend fun unregisterToken(token: String): EmptyResult<DataError.Remote> {
        return httpClient.delete(
            route = "/notification/$token"
        )
    }
}