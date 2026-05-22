package com.taoufikcode.core.domain.logging

interface KrossChatLogger {
    fun d(
        tag: String,
        message: () -> String,
    )

    fun i(
        tag: String,
        message: () -> String,
    )

    fun w(
        tag: String,
        message: () -> String,
    )

    fun e(
        tag: String,
        throwable: Throwable? = null,
        message: () -> String,
    )
}