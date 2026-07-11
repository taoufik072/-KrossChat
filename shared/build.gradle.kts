plugins {
    alias(libs.plugins.convention.cmp.application)
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
        }
        commonMain.dependencies {
            implementation(projects.core.designsystem)
            implementation(projects.core.data)
            implementation(projects.core.domain)
            implementation(projects.core.presentation)

            implementation(projects.feature.auth.domain)
            implementation(projects.feature.auth.presentation)

            implementation(projects.feature.chat.data)
            implementation(projects.feature.chat.database)
            implementation(projects.feature.chat.domain)
            implementation(projects.feature.chat.presentation)

            implementation(libs.jetbrains.compose.runtime)
            implementation(libs.jetbrains.compose.foundation)
            implementation(libs.jetbrains.compose.material3)
            implementation(libs.jetbrains.compose.ui)
            implementation(libs.jetbrains.compose.components.resources)
            implementation(libs.jetbrains.compose.ui.tooling.preview)
            implementation(libs.jetbrains.compose.viewmodel)
            implementation(libs.jetbrains.lifecycle.compose)
            implementation(libs.jetbrains.compose.navigation)
            implementation(libs.bundles.koin.common)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
