package com.taoufikcode.chat.data.di

import com.taoufikcode.chat.data.repository.ChatRepository
import com.taoufikcode.chat.domain.ChatService
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val chatDataModule = module {
    singleOf(::ChatRepository) bind ChatService::class
}