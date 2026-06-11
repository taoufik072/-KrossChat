package com.taoufikcode.chat.presentation.di

import com.taoufikcode.chat.presentation.chat_detail.ChatDetailViewModel
import com.taoufikcode.chat.presentation.chat_list.ChatListViewModel
import com.taoufikcode.chat.presentation.chat_list_detail.ChatListDetailViewModel
import com.taoufikcode.chat.presentation.chat_list_detail.add_participants.AddParticipantsViewModel
import com.taoufikcode.chat.presentation.chat_list_detail.create_chat.CreateChatViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val chatPresentationModule = module {
    viewModelOf(::ChatListDetailViewModel)
    viewModelOf(::CreateChatViewModel)
    viewModelOf(::ChatListViewModel)
    viewModelOf(::ChatDetailViewModel)
    viewModelOf(::AddParticipantsViewModel)


}