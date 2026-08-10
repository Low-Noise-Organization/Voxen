dependencies {
    implementation(project(":core"))
    implementation(project(":runtime"))
    implementation(project(":distribution"))
    implementation(project(":docker"))
    implementation(project(":plugins:api"))
    implementation("info.picocli:picocli:4.7.7")
    annotationProcessor("info.picocli:picocli-codegen:4.7.7")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "io.lownoise.voxen.cli.VoxenCli",
            "Implementation-Title" to "Voxen",
            "Implementation-Version" to project.version,
            "Implementation-Vendor" to "lownoise.io"
        )
    }
}

tasks.register<Jar>("fatJar") {
    dependsOn(tasks.jar)
    group = "build"
    description = "Build an executable fat JAR with all dependencies"
    archiveBaseName = "voxen"
    archiveVersion = project.version.toString()
    from(sourceSets.main.get().output)
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get()
            .filter { it.name.endsWith(".jar") && !it.name.contains("gradle") }
            .map { zipTree(it) }
    })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes(
            "Main-Class" to "io.lownoise.voxen.cli.VoxenCli",
            "Implementation-Title" to "Voxen",
            "Implementation-Version" to project.version,
            "Implementation-Vendor" to "lownoise.io"
        )
    }
}

tasks.named("jacocoTestCoverageVerification") {
    (this as org.gradle.testing.jacoco.tasks.JacocoCoverageVerification)
        .violationRules.rules.first()
        .limits.first().apply {
            minimum = "0.05".toBigDecimal()
        }
}
