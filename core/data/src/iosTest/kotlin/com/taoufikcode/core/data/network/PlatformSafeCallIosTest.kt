package com.taoufikcode.core.data.network

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.taoufikcode.core.data.testHttpClient
import com.taoufikcode.core.domain.util.DataError
import com.taoufikcode.core.domain.util.Result
import io.ktor.client.engine.darwin.DarwinHttpRequestException
import io.ktor.client.engine.mock.MockEngine
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.test.runTest
import platform.Foundation.NSError
import platform.Foundation.NSURLErrorDomain
import platform.Foundation.NSURLErrorNotConnectedToInternet
import platform.Foundation.NSURLErrorTimedOut
import kotlin.test.Test

/**
 * Covers the Darwin exception mapping of the iOS actual of [platformSafeCall].
 * Runs via iosSimulatorArm64Test.
 */
class PlatformSafeCallIosTest {

    private fun nsUrlError(code: Long): NSError =
        NSError.errorWithDomain(NSURLErrorDomain, code, null)

    private suspend fun requestWith(throwable: Throwable): Result<Unit, DataError.Remote> {
        val client = testHttpClient(MockEngine { throw throwable })
        return client.get(route = "/test")
    }

    @Test
    fun `not connected NSURLError maps to NO_INTERNET`() = runTest {
        val exception = DarwinHttpRequestException(nsUrlError(NSURLErrorNotConnectedToInternet))

        assertThat(requestWith(exception))
            .isEqualTo(Result.Failure(DataError.Remote.NO_INTERNET))
    }

    @Test
    fun `timed out NSURLError maps to REQUEST_TIMEOUT`() = runTest {
        val exception = DarwinHttpRequestException(nsUrlError(NSURLErrorTimedOut))

        assertThat(requestWith(exception))
            .isEqualTo(Result.Failure(DataError.Remote.REQUEST_TIMEOUT))
    }

    @Test
    fun `unmapped NSURLError code maps to UNKNOWN`() = runTest {
        val exception = DarwinHttpRequestException(nsUrlError(-9999L))

        assertThat(requestWith(exception))
            .isEqualTo(Result.Failure(DataError.Remote.UNKNOWN))
    }

    @Test
    fun `error outside NSURLErrorDomain maps to UNKNOWN`() = runTest {
        val exception = DarwinHttpRequestException(
            NSError.errorWithDomain("SomeOtherDomain", -1L, null)
        )

        assertThat(requestWith(exception))
            .isEqualTo(Result.Failure(DataError.Remote.UNKNOWN))
    }

    @Test
    fun `UnresolvedAddressException maps to NO_INTERNET`() = runTest {
        assertThat(requestWith(UnresolvedAddressException()))
            .isEqualTo(Result.Failure(DataError.Remote.NO_INTERNET))
    }
}
