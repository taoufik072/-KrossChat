package com.taoufikcode.presentation

import app.cash.turbine.ReceiveTurbine
import com.taoufikcode.core.domain.auth.AuthInfo
import com.taoufikcode.core.domain.auth.AuthService
import com.taoufikcode.core.domain.auth.SessionStorage
import com.taoufikcode.core.domain.auth.User
import com.taoufikcode.core.domain.util.DataError
import com.taoufikcode.core.domain.util.EmptyResult
import com.taoufikcode.core.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

val testAuthInfo = AuthInfo(
    accessToken = "access-token",
    refreshToken = "refresh-token",
    user = User(
        id = "user-1",
        email = "user@example.com",
        userName = "kross",
        hasVerifiedEmail = true,
        profilePictureUrl = null
    )
)

/** Awaits items until one matches [predicate], skipping intermediate emissions. */
suspend fun <T> ReceiveTurbine<T>.awaitUntil(predicate: (T) -> Boolean): T {
    while (true) {
        val item = awaitItem()
        if (predicate(item)) return item
    }
}

class FakeAuthService : AuthService {
    var loginResult: Result<AuthInfo, DataError.Remote> = Result.Success(testAuthInfo)
    var registerResult: EmptyResult<DataError.Remote> = Result.Success(Unit)

    val loginCalls = mutableListOf<Pair<String, String>>()
    val registerCalls = mutableListOf<Triple<String, String, String>>()

    override suspend fun login(
        email: String,
        password: String
    ): Result<AuthInfo, DataError.Remote> {
        loginCalls += email to password
        return loginResult
    }

    override suspend fun register(
        email: String,
        username: String,
        password: String
    ): EmptyResult<DataError.Remote> {
        registerCalls += Triple(email, username, password)
        return registerResult
    }

    override suspend fun verifyEmail(token: String): EmptyResult<DataError.Remote> =
        Result.Success(Unit)

    override suspend fun resendVerificationEmail(email: String): EmptyResult<DataError.Remote> =
        Result.Success(Unit)

    override suspend fun forgotPassword(email: String): EmptyResult<DataError.Remote> =
        Result.Success(Unit)

    override suspend fun resetPassword(
        newPassword: String,
        token: String
    ): EmptyResult<DataError.Remote> = Result.Success(Unit)

    override suspend fun changePassword(
        currentPassword: String,
        newPassword: String
    ): EmptyResult<DataError.Remote> = Result.Success(Unit)

    override suspend fun logout(refreshToken: String): EmptyResult<DataError.Remote> =
        Result.Success(Unit)
}

class FakeSessionStorage : SessionStorage {
    private val authInfoFlow = MutableStateFlow<AuthInfo?>(null)

    val storedAuthInfo: AuthInfo? get() = authInfoFlow.value

    override fun observeAuthInfo(): Flow<AuthInfo?> = authInfoFlow

    override suspend fun set(info: AuthInfo?) {
        authInfoFlow.value = info
    }
}
