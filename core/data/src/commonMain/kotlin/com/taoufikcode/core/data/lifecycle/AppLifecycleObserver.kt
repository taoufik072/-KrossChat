package com.taoufikcode.core.data.lifecycle

import kotlinx.coroutines.flow.Flow

expect class AppLifecycleObserver {
    val isInForeground: Flow<Boolean>
}