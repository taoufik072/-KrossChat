package com.taoufikcode.core.domain.util

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import kotlin.test.Test

class ResultTest {

    @Test
    fun `map transforms data on Success`() {
        val result: Result<Int, DataError.Remote> = Result.Success(21)

        val mapped = result.map { it * 2 }

        assertThat(mapped).isEqualTo(Result.Success(42))
    }

    @Test
    fun `map keeps error on Failure`() {
        val result: Result<Int, DataError.Remote> = Result.Failure(DataError.Remote.UNAUTHORIZED)

        val mapped = result.map { it * 2 }

        assertThat(mapped).isEqualTo(Result.Failure(DataError.Remote.UNAUTHORIZED))
    }

    @Test
    fun `onSuccess runs action and returns same result on Success`() {
        val result: Result<String, DataError.Remote> = Result.Success("data")
        var received: String? = null

        val returned = result.onSuccess { received = it }

        assertThat(received).isEqualTo("data")
        assertThat(returned).isEqualTo(result)
    }

    @Test
    fun `onSuccess does not run action on Failure`() {
        val result: Result<String, DataError.Remote> = Result.Failure(DataError.Remote.UNKNOWN)
        var invoked = false

        result.onSuccess { invoked = true }

        assertThat(invoked).isFalse()
    }

    @Test
    fun `onFailure runs action and returns same result on Failure`() {
        val result: Result<String, DataError.Remote> = Result.Failure(DataError.Remote.CONFLICT)
        var received: DataError.Remote? = null

        val returned = result.onFailure { received = it }

        assertThat(received).isEqualTo(DataError.Remote.CONFLICT)
        assertThat(returned).isEqualTo(result)
    }

    @Test
    fun `onFailure does not run action on Success`() {
        val result: Result<String, DataError.Remote> = Result.Success("data")
        var invoked = false

        result.onFailure { invoked = true }

        assertThat(invoked).isFalse()
    }

    @Test
    fun `asEmptyResult discards data on Success`() {
        val result: Result<String, DataError.Remote> = Result.Success("data")

        val empty = result.asEmptyResult()

        assertThat(empty).isEqualTo(Result.Success(Unit))
    }

    @Test
    fun `asEmptyResult keeps error on Failure`() {
        val result: Result<String, DataError.Remote> = Result.Failure(DataError.Remote.NOT_FOUND)

        val empty = result.asEmptyResult()

        assertThat(empty).isEqualTo(Result.Failure(DataError.Remote.NOT_FOUND))
    }

    @Test
    fun `chained operators short-circuit correctly`() {
        var successCount = 0
        var failureCount = 0

        Result.Success(1)
            .map { it + 1 }
            .onSuccess { successCount++ }
            .onFailure { failureCount++ }

        assertThat(successCount).isEqualTo(1)
        assertThat(failureCount).isEqualTo(0)
        assertThat(successCount == 1 && failureCount == 0).isTrue()
    }
}
