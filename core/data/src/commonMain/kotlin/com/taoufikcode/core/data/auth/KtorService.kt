package com.taoufikcode.core.data.auth

import com.taoufikcode.core.data.dto.requests.RegisterDto
import com.taoufikcode.core.data.network.post
import com.taoufikcode.core.domain.auth.AuthService
import com.taoufikcode.core.domain.util.DataError
import com.taoufikcode.core.domain.util.EmptyResult
import io.ktor.client.HttpClient

class KtorService(private val httpClient: HttpClient
): AuthService {

    override suspend fun register(
        email: String,
        username: String,
        password: String
    ): EmptyResult<DataError.Remote> {
        return httpClient.post(
            route = "/auth/register",
            body = RegisterDto(
                email = email,
                username = username,
                password = password
            )
        )
    }
}