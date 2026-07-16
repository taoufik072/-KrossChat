import com.taoufikcode.krosschat.convention.configureKotlinMultiplatform
import com.taoufikcode.krosschat.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Convention plugin applied in a module as `id("com.taoufikcode.kmp.library")`.
 * Base plugin for every KMP library module (domain/data layers).
 *
 * Equivalent to writing this directly in the module's build.gradle.kts:
 * ```
 * plugins {
 *     id("org.jetbrains.kotlin.multiplatform")
 *     id("com.android.kotlin.multiplatform.library")
 *     id("org.jetbrains.kotlin.plugin.serialization")
 * }
 *
 * kotlin {
 *     // Android target, iOS targets/framework and compiler flags — see the
 *     // expanded equivalent documented on [configureKotlinMultiplatform].
 *
 *     sourceSets {
 *         commonMain.dependencies {
 *             implementation(libs.kotlinx.serialization.json)
 *         }
 *         commonTest.dependencies {
 *             implementation(libs.kotlin.test)
 *         }
 *     }
 * }
 * ```
 */
@Suppress("unused") // implementationClass in /convention/build.gradle.kts
class KmpLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.multiplatform")
                apply("com.android.kotlin.multiplatform.library")
                apply("org.jetbrains.kotlin.plugin.serialization")
            }

            configureKotlinMultiplatform()
            dependencies {
                "commonMainImplementation"(
                    dependency = libs.findLibrary("kotlinx-serialization-json").get()
                )
                "commonTestImplementation"(dependency = libs.findLibrary("kotlin-test").get())
            }
        }
    }
}