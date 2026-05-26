package com.taoufikcode.core.data.di

import com.taoufikcode.core.data.auth.DataStoreSessionStorage
import com.taoufikcode.core.data.auth.KtorService
import com.taoufikcode.core.data.logging.KermitLogger
import com.taoufikcode.core.data.network.HttpClientFactory
import com.taoufikcode.core.domain.auth.AuthService
import com.taoufikcode.core.domain.auth.SessionStorage
import com.taoufikcode.core.domain.logging.KrossChatLogger
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformCoreModule: Module

val coreDataModule = module {
    includes(platformCoreModule)
    single<KrossChatLogger> { KermitLogger }
    single {
        HttpClientFactory(get(),get()).create(get())
    }
    singleOf(::KtorService) bind AuthService::class
    singleOf(::DataStoreSessionStorage) bind SessionStorage::class
}