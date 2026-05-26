package com.taoufikcode.core.data.auth

import com.taoufikcode.core.data.dto.AuthInfoDto
import com.taoufikcode.core.data.dto.requests.EmailRequest
import com.taoufikcode.core.data.dto.requests.LoginRequest
import com.taoufikcode.core.data.dto.requests.RegisterRequest
import com.taoufikcode.core.data.dto.toDomain
import com.taoufikcode.core.data.network.get
import com.taoufikcode.core.data.network.post
import com.taoufikcode.core.domain.auth.AuthInfo
import com.taoufikcode.core.domain.auth.AuthService
import com.taoufikcode.core.domain.util.DataError
import com.taoufikcode.core.domain.util.EmptyResult
import com.taoufikcode.core.domain.util.Result
import com.taoufikcode.core.domain.util.map
import io.ktor.client.HttpClient

class KtorService(private val httpClient: HttpClient
): AuthService {
    override suspend fun login(email: String, password: String) : Result<AuthInfo, DataError.Remote> {
        return httpClient.post<LoginRequest, AuthInfoDto>(
            route = "/auth/login",
            body = LoginRequest(
                email = email,
                password = password
            )
        ).map { authInfoDto->
            authInfoDto.toDomain()
        }
    }

    override suspend fun register(
        email: String,
        username: String,
        password: String
    ): EmptyResult<DataError.Remote> {
        return httpClient.post(
            route = "/auth/register",
            body = RegisterRequest(
                email = email,
                username = username,
                password = password
            )
        )
    }

    override suspend fun verifyEmail(token: String): EmptyResult<DataError.Remote> {
        return httpClient.get(
            route = "auth/verify",
            queryParams = mapOf("token" to token)
        )
    }

    override suspend fun resendVerificationEmail(email: String): EmptyResult<DataError.Remote> {
        return httpClient.post(
            route = "/auth/resend-verification",
            body = EmailRequest(email),
        )
    }
}