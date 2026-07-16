import com.android.build.api.dsl.ApplicationExtension
import com.taoufikcode.krosschat.convention.configureKotlinAndroid
import com.taoufikcode.krosschat.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * plugins {
 *     id("com.android.application")
 * }
 *
 * android {
 *     namespace = "com.taoufikcode.krosschat"
 *     compileSdk = libs.versions.projectCompileSdkVersion.get().toInt()
 *
 *     defaultConfig {
 *         applicationId = libs.versions.projectApplicationId.get()
 *         minSdk = libs.versions.projectMinSdkVersion.get().toInt()
 *         targetSdk = libs.versions.projectTargetSdkVersion.get().toInt()
 *         versionCode = libs.versions.projectVersionCode.get().toInt()
 *         versionName = libs.versions.projectVersionName.get()
 *     }
 *     packaging {
 *         resources {
 *             excludes += "/META-INF/AL2.0"
 *             excludes += "/META-INF/LGPL2.1"
 *         }
 *     }
 *     buildTypes {
 *         release {
 *             isMinifyEnabled = false
 *         }
 *     }
 *      [configureKotlinAndroid]
 * }
 */
@Suppress("unused") // implementationClass **  com.taoufikcode.android.application **
class AndroidApplicationPlugin: Plugin<Project>{

    override fun apply(target: Project) {
        with(target){
            with(pluginManager){
                apply("com.android.application")
            }
            extensions.configure<ApplicationExtension> {
                namespace = "com.taoufikcode.krosschat"
                compileSdk = libs.findVersion("projectCompileSdkVersion").get().toString().toInt()

                defaultConfig {
                    applicationId =libs.findVersion("projectApplicationId").get().toString()
                    minSdk = libs.findVersion("projectMinSdkVersion").get().toString().toInt()
                    targetSdk = libs.findVersion("projectTargetSdkVersion").get().toString().toInt()
                    versionCode = libs.findVersion("projectVersionCode").get().toString().toInt()
                    versionName = libs.findVersion("projectVersionName").get().toString()
                }
                packaging {
                    resources {
                        excludes.add("/META-INF/AL2.0")
                        excludes.add("/META-INF/LGPL2.1")
                    }
                }
                buildTypes {
                    getByName("release") {
                        isMinifyEnabled = false
                    }
                }
                configureKotlinAndroid(this)
            }
        }

    }
}