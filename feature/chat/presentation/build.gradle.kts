plugins {
    alias(libs.plugins.convention.cmp.feature)
}

kotlin {


    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                implementation(projects.core.domain)
                implementation(projects.feature.chat.domain)
                implementation(projects.core.presentation)
                implementation(projects.core.designsystem)
                // Add KMP dependencies here

                implementation(libs.jetbrains.compose.components.resources)
                implementation(libs.jetbrains.compose.ui.tooling.preview)

                implementation(libs.material3.adaptive)
                implementation(libs.material3.adaptive.layout)
                implementation(libs.material3.adaptive.navigation)
                implementation(libs.jetbrains.compose.backhandler)
                implementation(libs.kotlinx.datetime)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        androidMain {
            dependencies {

            }
        }


        iosMain {
            dependencies {

            }
        }
    }

}