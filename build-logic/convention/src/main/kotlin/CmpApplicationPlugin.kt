import com.taoufikcode.krosschat.convention.configureKotlinMultiplatform
import com.taoufikcode.krosschat.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * plugins {
 *     id("org.jetbrains.kotlin.multiplatform")
 *     id("com.android.kotlin.multiplatform.library")
 *     id("org.jetbrains.compose")
 *     id("org.jetbrains.kotlin.plugin.compose")
 *     id("org.jetbrains.kotlin.plugin.serialization")
 * }
 *
 * kotlin {
 *     //  [configureKotlinMultiplatform]
 * }
 *
 * dependencies {
 *     add("androidRuntimeClasspath", libs.androidx.compose.ui.tooling)
 * }
 */
@Suppress("unused") // implementationClass in ./convention/build.gradle.kts
class CmpApplicationPlugin: Plugin<Project> {

    override fun apply(target: Project) {
        with(target){
            with(pluginManager){
                apply("org.jetbrains.kotlin.multiplatform")
                apply("com.android.kotlin.multiplatform.library")
                apply("org.jetbrains.compose")
                apply("org.jetbrains.kotlin.plugin.compose")
                apply("org.jetbrains.kotlin.plugin.serialization")
            }
            configureKotlinMultiplatform()
            dependencies{
                "androidRuntimeClasspath"(libs.findLibrary("androidx-compose-ui-tooling").get())
            }
        }
    }
}