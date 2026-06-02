package com.taoufikcode.core.data.network

import com.taoufikcode.core.data.BuildKonfig
import com.taoufikcode.core.data.auth.DataStoreSessionStorage
import com.taoufikcode.core.data.dto.AuthInfoDto
import com.taoufikcode.core.data.dto.RefreshDto
import com.taoufikcode.core.data.dto.toDomain
import com.taoufikcode.core.domain.logging.KrossChatLogger
import com.taoufikcode.core.domain.util.onFailure
import com.taoufikcode.core.domain.util.onSuccess
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.request.header
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.Json

class HttpClientFactory(
    private val keylogger: KrossChatLogger,
    private val sessionStorage: DataStoreSessionStorage,
) {
    fun create(httpClientEngine: HttpClientEngine): HttpClient {
        return HttpClient(httpClientEngine) {
            install(ContentNegotiation) {
                json(
                    json = Json {
                        ignoreUnknownKeys = true
                    }
                )

            }

            install(HttpTimeout) {
                socketTimeoutMillis = 30_000L
                requestTimeoutMillis = 30_000L
            }
            install(Logging) {
                logger = object : io.ktor.client.plugins.logging.Logger {
                    override fun log(message: String) {
                        keylogger.i("Ktor logging") { message }
                    }
                }
                level = LogLevel.ALL
            }
            install(WebSockets) {
                pingIntervalMillis = 30_000L
            }
            defaultRequest {
                header("x-api-key", BuildKonfig.API_KEY)
                contentType(ContentType.Application.Json)
            }
            install(Auth) {
                bearer {
                    loadTokens {
                        sessionStorage.observeAuthInfo()
                            .firstOrNull()?.let {
                                BearerTokens(
                                    accessToken = it.accessToken,
                                    refreshToken = it.refreshToken
                                )
                            }
                    }
                    refreshTokens {
                        if (response.request.url.encodedPath.contains("auth/")) {
                            return@refreshTokens null
                        }
                        val authInfo = sessionStorage.observeAuthInfo().firstOrNull()
                        if (authInfo?.refreshToken.isNullOrBlank()) {
                            sessionStorage.set(null)
                            return@refreshTokens null
                        }
                        var bearerTokens: BearerTokens? = null

                        client.post<RefreshDto, AuthInfoDto>(
                            route = "/auth/refresh",
                            body = RefreshDto(authInfo.refreshToken),
                            builder = {
                                markAsRefreshTokenRequest()
                            }).onSuccess { newAuthInfo ->
                            newAuthInfo.apply {
                                sessionStorage.set(toDomain())
                                bearerTokens = BearerTokens(
                                    accessToken = accessToken,
                                    refreshToken = refreshToken
                                )
                            }
                        }.onFailure {
                            sessionStorage.set(null)
                        }
                        bearerTokens
                    }

                }
            }
        }

    }
}