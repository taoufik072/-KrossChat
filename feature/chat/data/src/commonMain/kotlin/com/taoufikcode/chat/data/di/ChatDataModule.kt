package com.taoufikcode.chat.data.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.taoufikcode.chat.data.network.IncomingMessageHandler
import com.taoufikcode.chat.data.network.KtorWebSocketConnector
import com.taoufikcode.chat.data.remote.ChatRemoteDataSource
import com.taoufikcode.chat.data.repository.ChatMessageRepositoryImpl
import com.taoufikcode.chat.data.repository.ChatRepositoryImpl
import com.taoufikcode.chat.database.DatabaseFactory
import com.taoufikcode.chat.domain.ChatMessageRepository
import com.taoufikcode.chat.domain.ChatRepository
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformChatDataModule: Module
val chatDataModule = module {
    includes(platformChatDataModule)
    singleOf(::ChatRepositoryImpl) bind ChatRepository::class
    singleOf(::ChatMessageRepositoryImpl) bind ChatMessageRepository::class
    singleOf(::ChatRemoteDataSource)
    single {
        get<DatabaseFactory>()
            .create()
            .setDriver(BundledSQLiteDriver())
            .build()
    }
    singleOf(::IncomingMessageHandler)
    singleOf(::KtorWebSocketConnector)
    single {
        Json {
            ignoreUnknownKeys = true
        }
    }

}