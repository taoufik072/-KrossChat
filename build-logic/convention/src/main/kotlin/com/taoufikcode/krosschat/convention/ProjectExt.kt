package com.taoufikcode.krosschat.convention

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

/**
 * Access to the "libs" version catalog from plugin code.
 * libs.findVersion("ktor").get()            //  libs.versions.ktor.get()
 * libs.findLibrary("ktor-client-core").get() //  libs.ktor.client.core
 * ```
 */
val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")