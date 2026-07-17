package com.taoufikcode.core.data.network

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.taoufikcode.core.data.respondJson
import com.taoufikcode.core.data.testHttpClient
import com.taoufikcode.core.domain.util.DataError
import com.taoufikcode.core.domain.util.Result
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import kotlin.test.Test

@Serializable
private data class TestResponseDto(val message: String)

class HttpClientExtTest {

    private fun clientRespondingWith(
        status: HttpStatusCode,
        body: String = ""
    ) = testHttpClient(
        MockEngine { respondJson(content = body, status = status) }
    )

    @Test
    fun `2xx response with valid body maps to Success`() = runTest {
        val client = clientRespondingWith(HttpStatusCode.OK, """{"message":"hello"}""")

        val result: Result<TestResponseDto, DataError.Remote> = client.get(route = "/test")

        assertThat(result).isEqualTo(Result.Success(TestResponseDto("hello")))
    }

    @Test
    fun `unknown fields in response body are ignored`() = runTest {
        val client = clientRespondingWith(
            HttpStatusCode.OK,
            """{"message":"hello","unexpected":42}"""
        )

        val result: Result<TestResponseDto, DataError.Remote> = client.get(route = "/test")

        assertThat(result).isEqualTo(Result.Success(TestResponseDto("hello")))
    }

    @Test
    fun `error status codes map to their DataError`() = runTest {
        val expectedErrors = mapOf(
            HttpStatusCode.BadRequest to DataError.Remote.BAD_REQUEST,
            HttpStatusCode.Unauthorized to DataError.Remote.UNAUTHORIZED,
            HttpStatusCode.Forbidden to DataError.Remote.FORBIDDEN,
            HttpStatusCode.NotFound to DataError.Remote.NOT_FOUND,
            HttpStatusCode.RequestTimeout to DataError.Remote.REQUEST_TIMEOUT,
            HttpStatusCode.Conflict to DataError.Remote.CONFLICT,
            HttpStatusCode.PayloadTooLarge to DataError.Remote.PAYLOAD_TOO_LARGE,
            HttpStatusCode.TooManyRequests to DataError.Remote.TOO_MANY_REQUESTS,
            HttpStatusCode.InternalServerError to DataError.Remote.SERVER_ERROR,
            HttpStatusCode.ServiceUnavailable to DataError.Remote.SERVICE_UNAVAILABLE
        )

        expectedErrors.forEach { (status, expectedError) ->
            val client = clientRespondingWith(status)

            val result: Result<TestResponseDto, DataError.Remote> = client.get(route = "/test")

            assertThat(result, name = "status ${status.value}")
                .isEqualTo(Result.Failure(expectedError))
        }
    }

    @Test
    fun `unmapped error status maps to UNKNOWN`() = runTest {
        val client = clientRespondingWith(HttpStatusCode.BadGateway)

        val result: Result<TestResponseDto, DataError.Remote> = client.get(route = "/test")

        assertThat(result).isEqualTo(Result.Failure(DataError.Remote.UNKNOWN))
    }

    @Test
    fun `2xx response without json content type maps to SERIALIZATION`() = runTest {
        val client = testHttpClient(
            MockEngine {
                // No content-type header -> no transformation registered for the body
                respond(content = "not json", status = HttpStatusCode.OK)
            }
        )

        val result: Result<TestResponseDto, DataError.Remote> = client.get(route = "/test")

        assertThat(result).isEqualTo(Result.Failure(DataError.Remote.SERIALIZATION))
    }

    @Test
    fun `query params are appended to the request`() = runTest {
        val engine = MockEngine { respondJson("""{"message":"ok"}""") }
        val client = testHttpClient(engine)

        client.get<TestResponseDto>(
            route = "/test",
            queryParams = mapOf("page" to 2, "query" to "abc")
        )

        val request = engine.requestHistory.single()
        assertThat(request.url.parameters["page"]).isEqualTo("2")
        assertThat(request.url.parameters["query"]).isEqualTo("abc")
    }

    @Test
    fun `constructRoute prefixes base url for relative routes`() {
        assertThat(constructRoute("/auth/login"))
            .isEqualTo("${UrlConstants.BASE_URL_HTTP}/auth/login")
        assertThat(constructRoute("auth/login"))
            .isEqualTo("${UrlConstants.BASE_URL_HTTP}/auth/login")
    }

    @Test
    fun `constructRoute keeps absolute urls untouched`() {
        val absolute = "${UrlConstants.BASE_URL_HTTP}/chat"

        assertThat(constructRoute(absolute)).isEqualTo(absolute)
    }
}
