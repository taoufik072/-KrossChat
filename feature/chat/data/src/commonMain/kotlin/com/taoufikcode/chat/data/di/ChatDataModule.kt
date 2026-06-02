package com.taoufikcode.chat.data.di

import com.taoufikcode.chat.data.repository.ChatParticipantRepository
import com.taoufikcode.chat.domain.ChatParticipantService
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val chatDataModule = module {
    singleOf(::ChatParticipantRepository) bind ChatParticipantService::class
}