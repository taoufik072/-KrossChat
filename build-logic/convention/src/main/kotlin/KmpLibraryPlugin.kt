import com.android.build.api.dsl.LibraryExtension
import com.taoufikcode.krosschat.convention.configureKotlinAndroid
import com.taoufikcode.krosschat.convention.configureKotlinMultiplatform
import com.taoufikcode.krosschat.convention.libs
import com.taoufikcode.krosschat.convention.pathToResourcePrefix
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class KmpLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.multiplatform")
                apply("org.jetbrains.kotlin.plugin.serialization")
            }

            configureKotlinMultiplatform()
            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                resourcePrefix = this@with.pathToResourcePrefix()
                //required to  make debug build of app run on ios simulator
                experimentalProperties["android.experimental.kmp.enableAndroidResource"] = true
            }
            dependencies {
                "commonMainImplementation"(
                    dependency = libs.findLibrary("kotlinx-serialization-json").get()
                )
                "commonTestImplementation"(dependency = libs.findLibrary("kotlin-test").get())
            }
        }
    }
}