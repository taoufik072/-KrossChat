package com.taoufikcode.krosschat.convention

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * kotlin {
 *     androidLibrary {
 *         namespace = "com.taoufikcode.feature.chat.data"
 *         compileSdk = libs.versions.projectCompileSdkVersion.get().toInt()
 *         minSdk = libs.versions.projectMinSdkVersion.get().toInt()
 *
 *         compilerOptions {
 *             jvmTarget.set(JvmTarget.JVM_17)
 *             freeCompilerArgs.add("-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi")
 *         }
 *         androidResources {
 *             enable = true
 *         }
 *         withHostTest {}
 *     }
 *     listOf(
 *         iosArm64(),
 *         iosSimulatorArm64()
 *     ).forEach { iosTarget ->
 *         iosTarget.binaries.framework {
 *             baseName = "FeatureChatData"
 *         }
 *     }
 *     compilerOptions {
 *         freeCompilerArgs.add("-Xexpect-actual-classes")
 *         freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
 *         freeCompilerArgs.add("-opt-in=kotlin.time.ExperimentalTime")
 *     }
 * }
 */
internal fun Project.configureKotlinMultiplatform(){
    extensions.configure<KotlinMultiplatformExtension> {
        (this as ExtensionAware).extensions.configure<KotlinMultiplatformAndroidLibraryTarget> {
            namespace = this@configureKotlinMultiplatform.pathToPackageName()
            compileSdk = libs.findVersion("projectCompileSdkVersion").get().toString().toInt()
            minSdk = libs.findVersion("projectMinSdkVersion").get().toString().toInt()

            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_17)
                freeCompilerArgs.add("-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi")
            }

            androidResources {
                enable = true
            }

            withHostTest {}
        }

        listOf(
            iosArm64(),
            iosSimulatorArm64()
        ).forEach { iosTarget ->
            iosTarget.binaries.framework {
                baseName =this@configureKotlinMultiplatform.pathToFrameworkName()
            }
        }
        compilerOptions{
            freeCompilerArgs.add("-Xexpect-actual-classes")
            freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
            freeCompilerArgs.add("-opt-in=kotlin.time.ExperimentalTime")
        }
    }

}