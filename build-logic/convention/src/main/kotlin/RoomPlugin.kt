import androidx.room.gradle.RoomExtension
import com.taoufikcode.krosschat.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

/**
 * plugins {
 *     id("com.google.devtools.ksp")
 *     id("androidx.room")
 * }
 *
 * room {
 *     schemaDirectory("$projectDir/schemas")
 * }
 *
 * kotlin {
 *     sourceSets {
 *         commonMain.dependencies {
 *             api(libs.androidx.room.runtime)
 *             api(libs.sqlite.bundled)
 *         }
 *     }
 * }
 *
 * dependencies {
 *     add("kspAndroid", libs.androidx.room.compiler)
 *     add("kspIosSimulatorArm64", libs.androidx.room.compiler)
 *     add("kspIosArm64", libs.androidx.room.compiler)
 * }
 */
@Suppress("unused") // implementationClass in /convention/build.gradle.kts
class RoomPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.google.devtools.ksp")
                apply("androidx.room")
            }
            extensions.configure<RoomExtension> {
                schemaDirectory("$projectDir/schemas")
            }
            dependencies {
                "commonMainApi"(libs.findLibrary("androidx-room-runtime").get())
                "commonMainApi"(libs.findLibrary("sqlite-bundled").get())
                //old
                //"ksp"(libs.findLibrary("androidx-room-compiler").get())

                "kspAndroid"(libs.findLibrary("androidx-room-compiler").get())
                "kspIosSimulatorArm64"(libs.findLibrary("androidx-room-compiler").get())
                "kspIosArm64"(libs.findLibrary("androidx-room-compiler").get())
            }
        }
    }
}