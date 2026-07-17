package com.taoufikcode.core.domain.util

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class PaginatorTest {

    @Test
    fun `loadNextItems delivers items and advances key`() = runTest {
        val requestedKeys = mutableListOf<Int>()
        val loadingUpdates = mutableListOf<Boolean>()
        var delivered: Pair<List<String>, Int>? = null

        val paginator = Paginator<Int, String>(
            initialKey = 0,
            onLoadUpdated = { loadingUpdates += it },
            onRequest = { key ->
                requestedKeys += key
                Result.Success(listOf("page-$key"))
            },
            getNextKey = { requestedKeys.last() + 1 },
            onError = { throw AssertionError("Unexpected error: $it") },
            onSuccess = { items, newKey -> delivered = items to newKey }
        )

        paginator.loadNextItems()

        assertThat(requestedKeys).isEqualTo(listOf(0))
        assertThat(delivered).isEqualTo(listOf("page-0") to 1)
        assertThat(loadingUpdates).isEqualTo(listOf(true, false))

        paginator.loadNextItems()

        assertThat(requestedKeys).isEqualTo(listOf(0, 1))
    }

    @Test
    fun `loadNextItems skips request when key did not advance`() = runTest {
        var requestCount = 0

        val paginator = Paginator<Int, String>(
            initialKey = 0,
            onLoadUpdated = {},
            onRequest = {
                requestCount++
                Result.Success(emptyList())
            },
            getNextKey = { 0 },
            onError = {},
            onSuccess = { _, _ -> }
        )

        paginator.loadNextItems()
        paginator.loadNextItems()

        assertThat(requestCount).isEqualTo(1)
    }

    @Test
    fun `loadNextItems reports failure as DataErrorException`() = runTest {
        val loadingUpdates = mutableListOf<Boolean>()
        var receivedError: Throwable? = null

        val paginator = Paginator<Int, String>(
            initialKey = 0,
            onLoadUpdated = { loadingUpdates += it },
            onRequest = { Result.Failure(DataError.Remote.SERVER_ERROR) },
            getNextKey = { 1 },
            onError = { receivedError = it },
            onSuccess = { _, _ -> throw AssertionError("Unexpected success") }
        )

        paginator.loadNextItems()

        assertThat(receivedError).isNotNull().isInstanceOf(DataErrorException::class)
        assertThat((receivedError as DataErrorException).error)
            .isEqualTo(DataError.Remote.SERVER_ERROR)
        assertThat(loadingUpdates).isEqualTo(listOf(true, false))
    }

    @Test
    fun `loadNextItems reports thrown exception via onError`() = runTest {
        val thrown = IllegalStateException("boom")
        var receivedError: Throwable? = null

        val paginator = Paginator<Int, String>(
            initialKey = 0,
            onLoadUpdated = {},
            onRequest = { throw thrown },
            getNextKey = { 1 },
            onError = { receivedError = it },
            onSuccess = { _, _ -> }
        )

        paginator.loadNextItems()

        assertThat(receivedError).isEqualTo(thrown)
    }

    @Test
    fun `concurrent loads trigger only one request`() = runTest {
        var requestCount = 0

        val paginator = Paginator<Int, String>(
            initialKey = 0,
            onLoadUpdated = {},
            onRequest = {
                requestCount++
                delay(100)
                Result.Success(emptyList())
            },
            getNextKey = { 1 },
            onError = {},
            onSuccess = { _, _ -> }
        )

        launch { paginator.loadNextItems() }
        launch { paginator.loadNextItems() }
        advanceUntilIdle()

        assertThat(requestCount).isEqualTo(1)
    }

    @Test
    fun `reset restores the initial key`() = runTest {
        val requestedKeys = mutableListOf<Int>()

        val paginator = Paginator<Int, String>(
            initialKey = 0,
            onLoadUpdated = {},
            onRequest = { key ->
                requestedKeys += key
                Result.Success(listOf("item"))
            },
            getNextKey = { requestedKeys.last() + 1 },
            onError = {},
            onSuccess = { _, _ -> }
        )

        paginator.loadNextItems()
        paginator.reset()
        paginator.loadNextItems()

        assertThat(requestedKeys).isEqualTo(listOf(0, 0))
    }
}
