package com.taoufikcode.core.data.notification

import com.taoufikcode.core.domain.notification.PushNotificationService
import kotlinx.coroutines.flow.Flow

expect class FirebasePushNotificationService : PushNotificationService {
    override fun observeDeviceToken(): Flow<String?>
}