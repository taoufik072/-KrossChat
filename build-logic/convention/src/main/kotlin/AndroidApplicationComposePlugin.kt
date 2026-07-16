import com.android.build.api.dsl.ApplicationExtension
import com.taoufikcode.krosschat.convention.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

/**
 * plugins {
 *     id("com.taoufikcode.android.application")
 *     id("org.jetbrains.kotlin.plugin.compose")
 * }
 * ```
 */
@Suppress("unused") // implementationClass in /convention/build.gradle.kts
class AndroidApplicationComposePlugin: Plugin<Project> {
    override fun apply(target: Project) {
        with(target){
            with(pluginManager){
                apply("com.taoufikcode.android.application")
                apply("org.jetbrains.kotlin.plugin.compose")
            }
            val extension = extensions.getByType<ApplicationExtension>()
            configureKotlinAndroid(extension)
        }
    }
}