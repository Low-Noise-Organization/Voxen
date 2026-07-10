package io.lownoise.voxen.plugins.java;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

class BuildToolDetectionTest {

    @Test
    void mavenShouldAcceptPomXml(@TempDir Path tempDir) throws Exception {
        Files.createFile(tempDir.resolve("pom.xml"));
        MavenBuildTool tool = new MavenBuildTool();
        assertThat(tool.accepts(tempDir)).isTrue();
    }

    @Test
    void mavenShouldRejectWithoutPomXml(@TempDir Path tempDir) {
        MavenBuildTool tool = new MavenBuildTool();
        assertThat(tool.accepts(tempDir)).isFalse();
    }

    @Test
    void gradleShouldAcceptBuildGradleKts(@TempDir Path tempDir) throws Exception {
        Files.createFile(tempDir.resolve("build.gradle.kts"));
        GradleBuildTool tool = new GradleBuildTool();
        assertThat(tool.accepts(tempDir)).isTrue();
    }

    @Test
    void gradleShouldAcceptBuildGradle(@TempDir Path tempDir) throws Exception {
        Files.createFile(tempDir.resolve("build.gradle"));
        GradleBuildTool tool = new GradleBuildTool();
        assertThat(tool.accepts(tempDir)).isTrue();
    }

    @Test
    void gradleShouldRejectWithoutBuildFile(@TempDir Path tempDir) {
        GradleBuildTool tool = new GradleBuildTool();
        assertThat(tool.accepts(tempDir)).isFalse();
    }
}
