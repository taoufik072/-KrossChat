package com.taoufikcode.core.data.di

import com.taoufikcode.core.data.logging.KermitLogger
import com.taoufikcode.core.data.network.HttpClientFactory
import com.taoufikcode.core.domain.logging.KrossChatLogger
import org.koin.core.module.Module
import org.koin.dsl.module

expect val platformCoreModule: Module

val coreDataModule = module {
    single<KrossChatLogger>{ KermitLogger }
    single {
        HttpClientFactory(get()).create() }
}