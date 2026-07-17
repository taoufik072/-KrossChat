package com.taoufikcode.core.data.repository

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.taoufikcode.core.data.dto.LoginDto
import com.taoufikcode.core.data.respondJson
import com.taoufikcode.core.data.testHttpClient
import com.taoufikcode.core.domain.auth.AuthInfo
import com.taoufikcode.core.domain.auth.User
import com.taoufikcode.core.domain.util.DataError
import com.taoufikcode.core.domain.util.Result
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.toByteArray
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test

class AuthRepositoryTest {

    private val authInfoJson = """
        {
            "accessToken": "access-token",
            "refreshToken": "refresh-token",
            "user": {
                "id": "user-1",
                "email": "user@example.com",
                "username": "kross",
                "hasVerifiedEmail": true
            }
        }
    """.trimIndent()

    @Test
    fun `login posts credentials and maps response to domain AuthInfo`() = runTest {
        val engine = MockEngine { respondJson(authInfoJson) }
        val repository = AuthRepository(testHttpClient(engine))

        val result = repository.login(email = "user@example.com", password = "Secret123")

        assertThat(result).isEqualTo(
            Result.Success(
                AuthInfo(
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
            )
        )

        val request = engine.requestHistory.single()
        assertThat(request.method).isEqualTo(HttpMethod.Post)
        assertThat(request.url.encodedPath).isEqualTo("/api/auth/login")
        val sentBody = Json.decodeFromString<LoginDto>(request.body.toByteArray().decodeToString())
        assertThat(sentBody).isEqualTo(LoginDto(email = "user@example.com", password = "Secret123"))
    }

    @Test
    fun `login with wrong credentials returns UNAUTHORIZED`() = runTest {
        val engine = MockEngine { respondJson("", status = HttpStatusCode.Unauthorized) }
        val repository = AuthRepository(testHttpClient(engine))

        val result = repository.login(email = "user@example.com", password = "wrong")

        assertThat(result).isEqualTo(Result.Failure(DataError.Remote.UNAUTHORIZED))
    }

    @Test
    fun `register success returns empty result`() = runTest {
        val engine = MockEngine { respondJson("", status = HttpStatusCode.OK) }
        val repository = AuthRepository(testHttpClient(engine))

        val result = repository.register(
            email = "new@example.com",
            username = "newuser",
            password = "Secret123"
        )

        assertThat(result).isEqualTo(Result.Success(Unit))
        assertThat(engine.requestHistory.single().url.encodedPath).isEqualTo("/api/auth/register")
    }

    @Test
    fun `register with existing account returns CONFLICT`() = runTest {
        val engine = MockEngine { respondJson("", status = HttpStatusCode.Conflict) }
        val repository = AuthRepository(testHttpClient(engine))

        val result = repository.register(
            email = "taken@example.com",
            username = "taken",
            password = "Secret123"
        )

        assertThat(result).isEqualTo(Result.Failure(DataError.Remote.CONFLICT))
    }

    @Test
    fun `verifyEmail sends token as query parameter`() = runTest {
        val engine = MockEngine { respondJson("", status = HttpStatusCode.OK) }
        val repository = AuthRepository(testHttpClient(engine))

        val result = repository.verifyEmail(token = "verify-token-123")

        assertThat(result).isEqualTo(Result.Success(Unit))
        val request = engine.requestHistory.single()
        assertThat(request.method).isEqualTo(HttpMethod.Get)
        assertThat(request.url.encodedPath).isEqualTo("/api/auth/verify")
        assertThat(request.url.parameters["token"]).isEqualTo("verify-token-123")
    }

    @Test
    fun `forgotPassword posts email and returns empty result`() = runTest {
        val engine = MockEngine { respondJson("", status = HttpStatusCode.OK) }
        val repository = AuthRepository(testHttpClient(engine))

        val result = repository.forgotPassword(email = "user@example.com")

        assertThat(result).isEqualTo(Result.Success(Unit))
        assertThat(engine.requestHistory.single().url.encodedPath)
            .isEqualTo("/api/auth/forgot-password")
    }

    @Test
    fun `server error surfaces as SERVER_ERROR`() = runTest {
        val engine = MockEngine {
            respondJson("", status = HttpStatusCode.InternalServerError)
        }
        val repository = AuthRepository(testHttpClient(engine))

        val result = repository.login(email = "user@example.com", password = "Secret123")

        assertThat(result).isEqualTo(Result.Failure(DataError.Remote.SERVER_ERROR))
    }
}
