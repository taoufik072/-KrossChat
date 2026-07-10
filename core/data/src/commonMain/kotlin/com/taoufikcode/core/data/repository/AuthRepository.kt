package com.taoufikcode.core.data.repository

import com.taoufikcode.core.data.dto.AuthInfoDto
import com.taoufikcode.core.data.dto.ChangePasswordDto
import com.taoufikcode.core.data.dto.EmailDto
import com.taoufikcode.core.data.dto.LoginDto
import com.taoufikcode.core.data.dto.RefreshDto
import com.taoufikcode.core.data.dto.RegisterDto
import com.taoufikcode.core.data.dto.ResetPasswordDto
import com.taoufikcode.core.data.dto.toDomain
import com.taoufikcode.core.data.network.get
import com.taoufikcode.core.data.network.post
import com.taoufikcode.core.domain.auth.AuthInfo
import com.taoufikcode.core.domain.auth.AuthService
import com.taoufikcode.core.domain.util.DataError
import com.taoufikcode.core.domain.util.EmptyResult
import com.taoufikcode.core.domain.util.Result
import com.taoufikcode.core.domain.util.map
import com.taoufikcode.core.domain.util.onSuccess
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.authProvider
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.request.post

class AuthRepository(private val httpClient: HttpClient
): AuthService {
    override suspend fun login(email: String, password: String) : Result<AuthInfo, DataError.Remote> {
        return httpClient.post<LoginDto, AuthInfoDto>(
            route = "/auth/login",
            body = LoginDto(
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
            body = RegisterDto(
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
            body = EmailDto(email),
        )
    }

    override suspend fun forgotPassword(email: String): EmptyResult<DataError.Remote> {
        return httpClient.post<EmailDto, Unit>(
            route = "/auth/forgot-password",
            body = EmailDto(email)
        )
    }

    override suspend fun resetPassword(
        newPassword: String,
        token: String
    ): EmptyResult<DataError.Remote> {
        return httpClient.post(
            route = "/auth/reset-password",
            body = ResetPasswordDto(
                newPassword = newPassword,
                token = token
            )
        )
    }
    override suspend fun changePassword(
        currentPassword: String,
        newPassword: String
    ): EmptyResult<DataError.Remote> {
        return httpClient.post(
            route = "/auth/change-password",
            body = ChangePasswordDto(
                oldPassword = currentPassword,
                newPassword = newPassword
            )
        )
    }
    override suspend fun logout(refreshToken: String): EmptyResult<DataError.Remote> {
        return httpClient.post<RefreshDto, Unit>(
            route = "/auth/logout",
            body = RefreshDto(refreshToken)
        ).onSuccess {
            httpClient.authProvider<BearerAuthProvider>()?.clearToken()
        }
    }
}