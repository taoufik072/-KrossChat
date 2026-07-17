package com.taoufikcode.core.data.network

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.taoufikcode.core.data.testHttpClient
import com.taoufikcode.core.domain.util.DataError
import com.taoufikcode.core.domain.util.Result
import io.ktor.client.engine.mock.MockEngine
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerializationException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.nio.channels.UnresolvedAddressException
import kotlin.test.Test

/**
 * Covers the exception mapping of the Android actual of [platformSafeCall].
 * Lives in androidHostTest because the thrown exception types are JVM-only.
 */
class PlatformSafeCallTest {

    private fun clientThrowing(throwable: Throwable) = testHttpClient(
        MockEngine { throw throwable }
    )

    private suspend fun requestWith(throwable: Throwable): Result<Unit, DataError.Remote> {
        return clientThrowing(throwable).get(route = "/test")
    }

    @Test
    fun `UnknownHostException maps to NO_INTERNET`() = runTest {
        assertThat(requestWith(UnknownHostException("no dns")))
            .isEqualTo(Result.Failure(DataError.Remote.NO_INTERNET))
    }

    @Test
    fun `UnresolvedAddressException maps to NO_INTERNET`() = runTest {
        assertThat(requestWith(UnresolvedAddressException()))
            .isEqualTo(Result.Failure(DataError.Remote.NO_INTERNET))
    }

    @Test
    fun `ConnectException maps to NO_INTERNET`() = runTest {
        assertThat(requestWith(ConnectException("refused")))
            .isEqualTo(Result.Failure(DataError.Remote.NO_INTERNET))
    }

    @Test
    fun `SocketTimeoutException maps to REQUEST_TIMEOUT`() = runTest {
        assertThat(requestWith(SocketTimeoutException("timed out")))
            .isEqualTo(Result.Failure(DataError.Remote.REQUEST_TIMEOUT))
    }

    @Test
    fun `SerializationException maps to SERIALIZATION`() = runTest {
        assertThat(requestWith(SerializationException("bad json")))
            .isEqualTo(Result.Failure(DataError.Remote.SERIALIZATION))
    }

    @Test
    fun `unexpected exception maps to UNKNOWN`() = runTest {
        assertThat(requestWith(IllegalStateException("boom")))
            .isEqualTo(Result.Failure(DataError.Remote.UNKNOWN))
    }
}
