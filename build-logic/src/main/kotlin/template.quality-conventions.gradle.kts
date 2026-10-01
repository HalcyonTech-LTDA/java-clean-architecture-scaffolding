import org.gradle.testing.jacoco.tasks.JacocoReport
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import info.solidsoft.gradle.pitest.PitestPluginExtension

plugins {
    jacoco
    id("info.solidsoft.pitest")
}

val jacocoExclusions = listOf(
    "**/*Application.class",
    "**/config/**",
    "**/*MongoEntity.class",
    "**/*Request*.class",
    "**/*Response*.class",
    "**/*Command*.class",
    "**/*DTO*.class",
    "**/*Dto*.class"
)

// -----------------------------------------------------------------------------
// Code Coverage (Jacoco)
// -----------------------------------------------------------------------------
tasks.named<JacocoReport>("jacocoTestReport") {
    dependsOn(tasks.named("test"), tasks.named("integrationTest"))
    executionData(
        tasks.named<Test>("test").map { it.extensions.getByType<JacocoTaskExtension>().destinationFile },
        tasks.named<Test>("integrationTest").map { it.extensions.getByType<JacocoTaskExtension>().destinationFile }
    )
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                exclude(jacocoExclusions)
            }
        })
    )
}

tasks.named<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
    dependsOn(tasks.named("test"), tasks.named("integrationTest"))
    executionData(
        tasks.named<Test>("test").map { it.extensions.getByType<JacocoTaskExtension>().destinationFile },
        tasks.named<Test>("integrationTest").map { it.extensions.getByType<JacocoTaskExtension>().destinationFile }
    )
    violationRules {
        rule {
            element = "BUNDLE"
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.90".toBigDecimal()
            }
            limit {
                counter = "BRANCH"
                value = "COVEREDRATIO"
                minimum = "0.90".toBigDecimal()
            }
        }
    }
    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                exclude(jacocoExclusions)
            }
        })
    )
}

tasks.named("check") {
    dependsOn(tasks.named("integrationTest"))
    dependsOn(tasks.named("jacocoTestReport"))
    dependsOn(tasks.named("jacocoTestCoverageVerification"))
}


// -----------------------------------------------------------------------------
// Mutation Testing (Pitest) - On-Demand & Core Domain Focused
// -----------------------------------------------------------------------------
extensions.configure<PitestPluginExtension> {
    junit5PluginVersion.set("1.2.1")
    targetClasses.set(
        listOf(
            "com.example.templatejava.*.domain.model.*",
            "com.example.templatejava.*.application.usecase.impl.*"
        )
    )
    targetTests.set(
        listOf(
            "com.example.templatejava.*.domain.*Test",
            "com.example.templatejava.*.application.*Test"
        )
    )
    threads.set(Runtime.getRuntime().availableProcessors())
    outputFormats.set(listOf("XML", "HTML"))
    timestampedReports.set(false)
    historyInputLocation.set(layout.buildDirectory.file("pitest/history").get().asFile)
    historyOutputLocation.set(layout.buildDirectory.file("pitest/history").get().asFile)
}

