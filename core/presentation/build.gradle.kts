@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType

plugins {
    alias(libs.plugins.convention.cmp.library)
}

kotlin {

    applyDefaultHierarchyTemplate {
        common {
            group("mobile") {
                // com.android.kotlin.multiplatform.library's target is not a `KotlinAndroidTarget`,
                // so withAndroidTarget() (which checks for that exact class) won't match it.
                withCompilations { it.target.platformType == KotlinPlatformType.androidJvm }
                withIos()
            }
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                implementation(projects.core.domain)
                implementation(libs.jetbrains.compose.components.resources)
                implementation(libs.material3.adaptive)
                implementation(libs.bundles.koin.common)
                implementation(libs.kotlinx.datetime)
            }
        }
        val mobileMain by getting {
            dependencies {
                implementation(libs.moko.permissions)
                implementation(libs.moko.permissions.compose)
                implementation(libs.moko.permissions.notifications)
            }
        }
    }

}