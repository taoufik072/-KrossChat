package com.taoufikcode.krosschat.di

import com.taoufikcode.krosschat.MainViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::MainViewModel)
}