package com.taoufikcode.core.presentation.permissions

import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.RequestCanceledException
import dev.icerock.moko.permissions.notifications.REMOTE_NOTIFICATION

actual class PermissionController(
    private val mokoPermissionsController: PermissionsController
) {
    actual suspend fun requestPermission(permissions: Permissions): PermissionState {
        return try {
            mokoPermissionsController.providePermission(permissions.toMokoPermission())
            PermissionState.GRANTED
        } catch (_: DeniedAlwaysException) {
            PermissionState.PERMANENTLY_DENIED
        } catch (_: DeniedException) {
            PermissionState.DENIED
        } catch (_: RequestCanceledException) {
            PermissionState.DENIED
        }
    }
}

fun Permissions.toMokoPermission(): Permission {
    return when (this) {
        Permissions.NOTIFICATIONS -> Permission.REMOTE_NOTIFICATION
    }
}