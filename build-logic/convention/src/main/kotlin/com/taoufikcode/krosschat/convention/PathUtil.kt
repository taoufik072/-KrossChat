package com.taoufikcode.krosschat.convention

import org.gradle.api.Project
import java.util.Locale

/**
 * Derives the Android namespace / BuildKonfig package from the module path
 *
 * ":feature:chat:data" -> "com.taoufikcode.feature.chat.data"
 */
fun Project.pathToPackageName(): String {
    val relativePackageName = path.replace(
        ":", "."
    ).lowercase()
    return "com.taoufikcode$relativePackageName"
}

/**
 *  `iosTarget.binaries.framework { baseName = ... }`.
 * ":feature:chat:data" -> "FeatureChatData"
 */
fun Project.pathToFrameworkName(): String {
    val parts = this.path.split(":", "-", "_").filter { it.isNotEmpty() }
    val result = parts.joinToString("") { part ->
        part.replaceFirstChar {
            it.titlecase(Locale.ROOT)
        }
    }
    return result
}