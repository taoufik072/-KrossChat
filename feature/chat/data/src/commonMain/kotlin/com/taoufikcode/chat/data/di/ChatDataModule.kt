package com.taoufikcode.chat.data.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.taoufikcode.chat.data.repository.ChatRepository
import com.taoufikcode.chat.database.DatabaseFactory
import com.taoufikcode.chat.domain.ChatService
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformChatDataModule: Module
val chatDataModule = module {
    includes(platformChatDataModule)
    singleOf(::ChatRepository) bind ChatService::class
    single {
        get<DatabaseFactory>()
            .create()
            .setDriver(BundledSQLiteDriver())
            .build()
    }
}