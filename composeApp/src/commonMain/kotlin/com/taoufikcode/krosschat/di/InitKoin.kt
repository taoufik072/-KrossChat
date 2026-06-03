package com.taoufikcode.krosschat.di

import com.taoufikcode.chat.data.di.chatDataModule
import com.taoufikcode.chat.presentation.di.chatPresentationModule
import com.taoufikcode.core.data.di.coreDataModule
import com.taoufikcode.chat.data.platform
import com.taoufikcode.core.presentation.di.corePresentationModule
import com.taoufikcode.presentation.di.authPresentationModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(
            coreDataModule,
            authPresentationModule,
            appModule,
            corePresentationModule,
            chatPresentationModule,
            chatDataModule
        )
    }
}