package io.lownoise.voxen.runtime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

class JavaRuntimePluginTest {

    private final JavaRuntimePlugin plugin = new JavaRuntimePlugin();

    @Test
    void runtimeReturnsJava() {
        assertThat(plugin.runtime()).isEqualTo("java");
    }

    @Test
    void detectReturnsTrueForPomXml(@TempDir Path dir) throws Exception {
        Files.createFile(dir.resolve("pom.xml"));
        assertThat(plugin.detect(dir)).isTrue();
    }

    @Test
    void detectReturnsTrueForBuildGradle(@TempDir Path dir) throws Exception {
        Files.createFile(dir.resolve("build.gradle"));
        assertThat(plugin.detect(dir)).isTrue();
    }

    @Test
    void detectReturnsTrueForBuildGradleKts(@TempDir Path dir) throws Exception {
        Files.createFile(dir.resolve("build.gradle.kts"));
        assertThat(plugin.detect(dir)).isTrue();
    }

    @Test
    void detectReturnsFalseForEmptyDir(@TempDir Path dir) {
        assertThat(plugin.detect(dir)).isFalse();
    }

    @Test
    void buildCommandUsesMavenForPom(@TempDir Path dir) throws Exception {
        Files.createFile(dir.resolve("pom.xml"));
        assertThat(plugin.buildCommand(dir)).contains("mvn");
    }

    @Test
    void buildCommandUsesGradlewWhenPresent(@TempDir Path dir) throws Exception {
        Files.createFile(dir.resolve("build.gradle"));
        Files.createFile(dir.resolve("gradlew"));
        assertThat(plugin.buildCommand(dir)).contains("./gradlew");
    }

    @Test
    void buildCommandDefaultsToGradle(@TempDir Path dir) throws Exception {
        Files.createFile(dir.resolve("build.gradle"));
        assertThat(plugin.buildCommand(dir)).contains("gradle");
    }

    @Test
    void defaultArtifactPattern() {
        assertThat(plugin.defaultArtifactPattern()).isEqualTo("target/*.jar");
    }

    @Test
    void buildImage() {
        assertThat(plugin.buildImage()).contains("temurin");
    }
}
