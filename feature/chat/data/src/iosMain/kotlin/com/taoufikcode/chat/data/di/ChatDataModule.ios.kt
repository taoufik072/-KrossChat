package com.taoufikcode.chat.data.di

import com.taoufikcode.chat.database.DatabaseFactory
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformChatDataModule: Module = module {
    single { DatabaseFactory() }
}