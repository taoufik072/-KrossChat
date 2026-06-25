package com.taoufikcode.chat.data.di

import com.taoufikcode.chat.data.network.ConnectionErrorHandler
import com.taoufikcode.chat.database.DatabaseFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val platformChatDataModule = module {
    single { DatabaseFactory(androidContext()) }
    singleOf(::ConnectionErrorHandler)

}