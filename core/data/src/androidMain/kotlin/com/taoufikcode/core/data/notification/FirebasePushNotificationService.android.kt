package com.taoufikcode.core.data.notification

import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import com.taoufikcode.core.domain.logging.KrossChatLogger
import com.taoufikcode.core.domain.notification.PushNotificationService
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

actual class FirebasePushNotificationService(
    private val logger: KrossChatLogger
) : PushNotificationService {

    actual override fun observeDeviceToken(): Flow<String?> = flow {
        try {
            val fcmToken = Firebase.messaging.token.await()
            logger.i(tag = "FirebasePushNotificationService") { "Initial FCM token received: $fcmToken" }
            emit(fcmToken)
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            logger.e(
                tag = "FirebasePushNotificationService",
                throwable = e
            ) { "Failed to get FCM token" }
            emit(null)
        }
    }
}