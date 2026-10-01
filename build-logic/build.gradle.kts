plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation(libs.plugin.spotless)
    implementation(libs.plugin.pitest)
    implementation(libs.plugin.cyclonedx)
}
