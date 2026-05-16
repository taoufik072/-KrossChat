import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins{
    `kotlin-dsl`
}
group="com.taoufikcode.convention.buildlogic"

dependencies{
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.androidx.room.gradle.plugin)
    implementation(libs.buildkonfig.gradlePlugin)
    implementation(libs.buildkonfig.compiler)
}
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}
tasks{
    validatePlugins{
        enableStricterValidation = true
        failOnWarning=true
    }
}
gradlePlugin {
    plugins{
        register("androidApplication"){
            id= "com.taoufikcode.android.application"
            implementationClass= "AndroidApplicationPlugin"
        }

        register("androidComposeApplication"){
            id= "com.taoufikcode.android.application.compose"
            implementationClass= "AndroidApplicationComposePlugin"
        }

        register("cmpApplication"){
            id= "com.taoufikcode.cmp.application"
            implementationClass= "CmpApplicationPlugin"
        }
        register("KmpLibrary"){
            id= "com.taoufikcode.kmp.library"
            implementationClass= "KmpLibraryPlugin"
        }
        register("cmpLibrary"){
            id= "com.taoufikcode.cmp.library"
            implementationClass= "CmpLibraryPlugin"
        }
        register("cmpFeature"){
            id= "com.taoufikcode.cmp.feature"
            implementationClass= "CmpFeaturePlugin"
        }
        register("buildKonfig"){
            id= "com.taoufikcode.buildKonfig"
            implementationClass= "BuildKonfigPlugin"
        }
        register("room"){
            id= "com.taoufikcode.room"
            implementationClass= "RoomPlugin"
        }
    }
}