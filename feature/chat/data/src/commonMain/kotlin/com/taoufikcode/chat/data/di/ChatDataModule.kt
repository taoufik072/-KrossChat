package com.taoufikcode.chat.data.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.taoufikcode.chat.data.network.ConnectionRetryHandler
import com.taoufikcode.chat.data.network.KtorWebSocketConnector
import com.taoufikcode.chat.data.network.WebSocketChatConnectionClient
import com.taoufikcode.chat.data.repository.ChatMessageRepositoryImpl
import com.taoufikcode.chat.data.repository.ChatRepositoryImpl
import com.taoufikcode.chat.data.services.ChatRemoteDataSource
import com.taoufikcode.chat.data.services.ChatSyncData
import com.taoufikcode.chat.database.DatabaseFactory
import com.taoufikcode.chat.domain.repository.ChatMessageRepository
import com.taoufikcode.chat.domain.repository.ChatRepository
import com.taoufikcode.chat.domain.service.ChatConnectionClient
import com.taoufikcode.chat.domain.service.ChatSyncService
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
    singleOf(::WebSocketChatConnectionClient) bind ChatConnectionClient::class
    singleOf(::ChatSyncData) bind ChatSyncService::class
    singleOf(::ConnectionRetryHandler)
    singleOf(::KtorWebSocketConnector)
    singleOf(::ChatRemoteDataSource)
    single {
        get<DatabaseFactory>()
            .create()
            .setDriver(BundledSQLiteDriver())
            .build()
    }
    single {
        Json {
            ignoreUnknownKeys = true
        }
    }

}