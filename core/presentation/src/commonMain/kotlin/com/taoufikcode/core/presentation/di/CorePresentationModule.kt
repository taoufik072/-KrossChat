package com.taoufikcode.core.presentation.di

import com.taoufikcode.core.presentation.utils.ScopedStoreRegistryViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val corePresentationModule = module {
    viewModelOf(::ScopedStoreRegistryViewModel)
}