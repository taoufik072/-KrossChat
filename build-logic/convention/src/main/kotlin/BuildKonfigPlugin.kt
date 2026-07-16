import com.codingfeline.buildkonfig.compiler.FieldSpec
import com.codingfeline.buildkonfig.gradle.BuildKonfigExtension
import com.taoufikcode.krosschat.convention.pathToPackageName
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.Actions.with
import org.gradle.kotlin.dsl.configure
import java.io.File
import java.util.Properties

/**
 * import com.codingfeline.buildkonfig.compiler.FieldSpec
 * import java.util.Properties
 *
 * plugins {
 *     id("com.codingfeline.buildkonfig")
 * }
 *
 * buildkonfig {
 *     packageName = "com.taoufikcode.core.data"
 *
 *     defaultConfigs {
 *         val localProperties = Properties().apply {
 *             val file = rootDir.resolve("local.properties")
 *             if (file.exists()) file.inputStream().use { load(it) }
 *         }
 *         val apiKey = localProperties.getProperty("API_KEY")
 *             ?: error("missing API_KEY property in local.properties")
 *         buildConfigField(FieldSpec.Type.STRING, "API_KEY", apiKey)
 *     }
 * }
 * ```
 */
@Suppress("unused") // implementationClass in ./convention/build.gradle.kts
class BuildKonfigPlugin : Plugin<Project> {
    override fun apply(target: Project) {

        with(target) {
            with(pluginManager) {
                apply("com.codingfeline.buildkonfig")
            }
            extensions.configure<BuildKonfigExtension> {
                packageName = target.pathToPackageName()
                defaultConfigs {
                    val localProperties = Properties().apply {
                        val localPropertiesFile = File(rootDir, "local.properties")
                        if (localPropertiesFile.exists()) {
                            localPropertiesFile.inputStream().use { load(it) }
                        }
                    }
                    val apiKey =
                        localProperties.getProperty("API_KEY") ?: throw IllegalStateException(
                            "missing API_KEY property in local.properties"
                        )
                    buildConfigField(FieldSpec.Type.STRING,"API_KEY",apiKey)
                }
            }
        }
    }
}