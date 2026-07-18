import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.room)
    alias(libs.plugins.detekt)
    alias(libs.plugins.sonarqube)
}

detekt {
    buildUponDefaultConfig = true
    config.setFrom("$rootDir/config/detekt/detekt.yml")
    baseline = file("$rootDir/config/detekt/baseline.xml")
    source.setFrom(files(rootDir))
    parallel = true
}

tasks.withType<Detekt>().configureEach {
    include("**/src/*Main/kotlin/**", "**/src/*Test/kotlin/**", "**/src/main/kotlin/**")
    exclude("**/build/**", "**/build-logic/**", "**/iosApp/**", "**/*.kts")
    reports {
        xml.required.set(true)
        html.required.set(true)
        sarif.required.set(false)
        md.required.set(false)
    }
}

tasks.withType<DetektCreateBaselineTask>().configureEach {
    include("**/src/*Main/kotlin/**", "**/src/*Test/kotlin/**", "**/src/main/kotlin/**")
    exclude("**/build/**", "**/build-logic/**", "**/iosApp/**", "**/*.kts")
}

sonar {
    properties {
        property("sonar.projectKey", providers.gradleProperty("sonarProjectKey").get())
        property("sonar.organization", providers.gradleProperty("sonarOrganization").get())
        property("sonar.host.url", providers.gradleProperty("sonarHostUrl").get())
        property("sonar.coverage.jacoco.xmlReportPaths", "$rootDir/build/reports/kover/report.xml")
        property("sonar.kotlin.detekt.reportPaths", "$rootDir/build/reports/detekt/detekt.xml")
        property("sonar.exclusions", "**/build/**")
    }
}

subprojects {
    sonar {
        properties {
            val sourceDirs = listOf(
                "src/commonMain/kotlin",
                "src/androidMain/kotlin",
                "src/iosMain/kotlin",
                "src/mobileMain/kotlin",
                "src/main/kotlin",
            ).filter { file(it).isDirectory }
            val testDirs = listOf(
                "src/commonTest/kotlin",
                "src/androidHostTest/kotlin",
                "src/iosTest/kotlin",
            ).filter { file(it).isDirectory }

            property("sonar.sources", sourceDirs.joinToString(","))
            property("sonar.tests", testDirs.joinToString(","))
        }
    }
}
