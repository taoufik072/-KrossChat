package com.taoufikcode.krosschat.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * Shared Android (non-KMP) configuration used by the android.application plugins.
 * Takes [CommonExtension] so it works for both `com.android.application` and
 * `com.android.library` modules.
 *
 * Equivalent to writing this directly in the module's build.gradle.kts:
 * ```
 * android {
 *     compileSdk = libs.versions.projectCompileSdkVersion.get().toInt()
 *     defaultConfig {
 *         minSdk = libs.versions.projectMinSdkVersion.get().toInt()
 *     }
 *     compileOptions {
 *         sourceCompatibility = JavaVersion.VERSION_17
 *         targetCompatibility = JavaVersion.VERSION_17
 *         isCoreLibraryDesugaringEnabled = true
 *     }
 * }
 *
 * dependencies {
 *     coreLibraryDesugaring(libs.android.desugarJdkLibs)
 *     implementation(libs.kotlinx.coroutines.core)
 * }
 * ```
 */
internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension
) {
    with(commonExtension) {
        compileSdk = libs.findVersion("projectCompileSdkVersion").get().toString().toInt()
        defaultConfig.minSdk=  libs.findVersion("projectMinSdkVersion").get().toString().toInt()

        compileOptions.apply {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
            isCoreLibraryDesugaringEnabled = true
        }
        configureKotlin()
        dependencies {
            "coreLibraryDesugaring"(libs.findLibrary("android-desugarJdkLibs").get())
            "implementation"(libs.findLibrary("kotlinx-coroutines-core").get())
        }
    }
}

/**
 * Equivalent to writing this directly in the module's build.gradle.kts:
 * ```
 * tasks.withType<KotlinCompile>().configureEach {
 *     compilerOptions {
 *         jvmTarget.set(JvmTarget.JVM_17)
 *         freeCompilerArgs.add("-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi")
 *     }
 * }
 * ```
 */
internal fun Project.configureKotlin() {
    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
            freeCompilerArgs.add(
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
            )
        }

    }
}