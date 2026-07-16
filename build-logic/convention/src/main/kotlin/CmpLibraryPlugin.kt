import com.taoufikcode.krosschat.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.Actions.with
import org.gradle.kotlin.dsl.dependencies

/**
 * plugins {
 *     id("com.taoufikcode.kmp.library")
 *     id("org.jetbrains.kotlin.plugin.compose")
 *     id("org.jetbrains.compose")
 * }
 *
 * kotlin {
 *     sourceSets {
 *         commonMain.dependencies {
 *             implementation(compose.ui)
 *             implementation(compose.foundation)
 *             implementation(compose.material3)
 *             implementation(compose.materialIconsCore)
 *         }
 *         androidMain.dependencies {
 *             implementation(libs.androidx.activity.compose)
 *         }
 *     }
 * }
 *
 * dependencies {
 *     add("androidRuntimeClasspath", libs.androidx.compose.ui.tooling)
 * }
 */
@Suppress("unused") // implementationClass in /convention/build.gradle.kts
class CmpLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with( target){
            with(pluginManager){
                apply ("com.taoufikcode.kmp.library")
                apply("org.jetbrains.kotlin.plugin.compose")
                apply("org.jetbrains.compose")
                apply("org.jetbrains.kotlin.plugin.serialization")
            }
            dependencies{
                "commonMainImplementation"(libs.findLibrary("jetbrains-compose-ui").get())
                "commonMainImplementation"(libs.findLibrary("jetbrains-compose-foundation").get())
                "commonMainImplementation"(libs.findLibrary("jetbrains-compose-material3").get())
                "commonMainImplementation"(libs.findLibrary("jetbrains-compose-material-icons-core").get())
                "androidRuntimeClasspath"(libs.findLibrary("androidx-compose-ui-tooling").get())
                "androidMainImplementation"(libs.findLibrary("androidx-activity-compose").get())
            }
        }
        
    }
}
