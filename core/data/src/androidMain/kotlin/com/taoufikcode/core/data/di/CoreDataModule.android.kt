package com.taoufikcode.core.data.di

import io.ktor.client.engine.HttpClientEngine
import org.koin.dsl.module

actual val platformCoreModule = module {
    single<HttpClientEngine> { io.ktor.client.engine.okhttp.OkHttp.create() }
}