plugins {
    id("org.cyclonedx.bom")
}

tasks.named<org.cyclonedx.gradle.CyclonedxDirectTask>("cyclonedxDirectBom") {
    includeConfigs.set(listOf("runtimeClasspath"))
    skipConfigs.set(
        listOf(
            "testCompileClasspath",
            "testRuntimeClasspath",
            "integrationTestCompileClasspath",
            "integrationTestRuntimeClasspath"
        )
    )
    projectType.set(org.cyclonedx.model.Component.Type.APPLICATION)
    schemaVersion.set(org.cyclonedx.Version.VERSION_16)
}

tasks.named<org.cyclonedx.gradle.CyclonedxAggregateTask>("cyclonedxBom") {
    projectType.set(org.cyclonedx.model.Component.Type.APPLICATION)
    schemaVersion.set(org.cyclonedx.Version.VERSION_16)
}

tasks.named("check") {
    dependsOn("cyclonedxBom")
}
