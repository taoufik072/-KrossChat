package com.taoufikcode.chat.presentation.di

import com.taoufikcode.chat.presentation.chat_list_detail.ChatListDetailViewModel
import com.taoufikcode.chat.presentation.create_chat.CreateChatViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val chatPresentationModule = module {
    singleOf(::ChatListDetailViewModel)
    singleOf(::CreateChatViewModel)
}