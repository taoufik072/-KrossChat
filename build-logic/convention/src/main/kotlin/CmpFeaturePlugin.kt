import com.taoufikcode.krosschat.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * plugins {
 *     id("com.taoufikcode.cmp.library")
 * }
 *
 * kotlin {
 *     sourceSets {
 *         commonMain.dependencies {
 *             implementation(projects.core.presentation)
 *             implementation(projects.core.designsystem)
 *
 *             implementation(platform(libs.koin.bom))
 *             implementation(libs.koin.compose)
 *             implementation(libs.koin.compose.viewmodel)
 *
 *             implementation(libs.jetbrains.compose.runtime)
 *             implementation(libs.jetbrains.compose.viewmodel)
 *             implementation(libs.jetbrains.lifecycle.viewmodel)
 *             implementation(libs.jetbrains.lifecycle.compose)
 *
 *             implementation(libs.jetbrains.lifecycle.viewmodel.savedstate)
 *             implementation(libs.jetbrains.savedstate)
 *             implementation(libs.jetbrains.bundle)
 *             implementation(libs.jetbrains.compose.navigation)
 *         }
 *         androidMain.dependencies {
 *             implementation(platform(libs.koin.bom))
 *             implementation(libs.koin.android)
 *             implementation(libs.koin.androidx.compose)
 *             implementation(libs.koin.androidx.navigation)
 *             implementation(libs.koin.core.viewmodel)
 *         }
 *     }
 * }
 */
@Suppress("unused") // implementationClass in /convention/build.gradle.kts
class CmpFeaturePlugin: Plugin<Project> {
    override fun apply(target: Project) {
        with(target){
            with(pluginManager){
                apply("com.taoufikcode.cmp.library")
            }
            dependencies{
                "commonMainImplementation"(project(":core:presentation"))
                "commonMainImplementation"(project(":core:designsystem"))

                "commonMainImplementation"(platform(libs.findLibrary("koin-bom").get()))
                "androidMainImplementation"(platform(libs.findLibrary("koin-bom").get()))

                "commonMainImplementation"(libs.findLibrary("koin-compose").get())
                "commonMainImplementation"(libs.findLibrary("koin-compose-viewmodel").get())

                "commonMainImplementation"(libs.findLibrary("jetbrains-compose-runtime").get())
                "commonMainImplementation"(libs.findLibrary("jetbrains-compose-viewmodel").get())
                "commonMainImplementation"(libs.findLibrary("jetbrains-lifecycle-viewmodel").get())
                "commonMainImplementation"(libs.findLibrary("jetbrains-lifecycle-compose").get())

                "commonMainImplementation"(libs.findLibrary("jetbrains-lifecycle-viewmodel-savedstate").get())
                "commonMainImplementation"(libs.findLibrary("jetbrains-savedstate").get())
                "commonMainImplementation"(libs.findLibrary("jetbrains-bundle").get())
                "commonMainImplementation"(libs.findLibrary("jetbrains-compose-navigation").get())

                "androidMainImplementation"(libs.findLibrary("koin-android").get())
                "androidMainImplementation"(libs.findLibrary("koin-androidx-compose").get())
                "androidMainImplementation"(libs.findLibrary("koin-androidx-navigation").get())
                "androidMainImplementation"(libs.findLibrary("koin-core-viewmodel").get())
            }
        }

    }
}