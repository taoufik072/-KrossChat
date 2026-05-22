package com.taoufikcode.core.data.logging

import co.touchlab.kermit.Logger.Companion.withTag
import com.taoufikcode.core.domain.logging.KrossChatLogger

object KermitLogger : KrossChatLogger{
    override fun d(
        tag: String,
        message: () -> String,
    ) = withTag(tag).d(message())

    override fun i(
        tag: String,
        message: () -> String,
    ) = withTag(tag).i(message())

    override fun w(
        tag: String,
        message: () -> String,
    ) = withTag(tag).w(message())

    override fun e(
        tag: String,
        throwable: Throwable?,
        message: () -> String,
    ) = withTag(tag).e(message(), throwable = throwable)
}