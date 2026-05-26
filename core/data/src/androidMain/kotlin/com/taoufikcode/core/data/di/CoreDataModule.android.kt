package com.taoufikcode.core.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.taoufikcode.core.data.auth.createDataStore
import io.ktor.client.engine.HttpClientEngine
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val platformCoreModule = module {
    single<HttpClientEngine> { io.ktor.client.engine.okhttp.OkHttp.create() }
    single< DataStore<Preferences>> {
        createDataStore(androidContext())
    }
}