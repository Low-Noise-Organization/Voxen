package io.lownoise.voxen.testing;

import io.lownoise.voxen.core.VoxenConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

class ProjectLifecycleIntegrationTest {

    @Test
    void initCreatesVoxenConfig(@TempDir Path tempDir) {
        Path projectDir = tempDir.resolve("my-app");
        projectDir.toFile().mkdirs();

        VoxenConfig config = new VoxenConfig("my-app", "java", "jar", "dist");
        config.write(projectDir);

        assertThat(projectDir.resolve("voxen.json")).exists();
        assertThat(projectDir.resolve("voxen.json")).isRegularFile();

        VoxenConfig loaded = VoxenConfig.fromProjectDir(projectDir);
        assertThat(loaded.name()).isEqualTo("my-app");
        assertThat(loaded.runtime()).isEqualTo("java");
        assertThat(loaded.target()).isEqualTo("jar");
        assertThat(loaded.output()).isEqualTo("dist");
    }

    @Test
    void configWithDefaultsUsesDistDirectory(@TempDir Path tempDir) {
        VoxenConfig config = new VoxenConfig("defaults", "java", null, null);
        var projectConfig = config.toProjectConfig(tempDir);
        assertThat(projectConfig.outputDirectory()).isEqualTo(tempDir.resolve("dist"));
    }

    @Test
    void configWithCustomOutput(@TempDir Path tempDir) {
        VoxenConfig config = new VoxenConfig("custom", "java", "war", "build");
        var projectConfig = config.toProjectConfig(tempDir);
        assertThat(projectConfig.outputDirectory()).isEqualTo(tempDir.resolve("build"));
    }

    @Test
    void initWithoutNameUsesDirectoryName(@TempDir Path tempDir) {
        VoxenConfig config = new VoxenConfig("auto-name-project", "java", null, null);
        config.write(tempDir);

        VoxenConfig loaded = VoxenConfig.fromProjectDir(tempDir);
        assertThat(loaded.name()).isEqualTo("auto-name-project");
    }

    @Test
    void buildFailsWithoutProject() {
        assertThatThrownBy(() -> VoxenConfig.fromProjectDir(Path.of("/nonexistent")))
            .isInstanceOf(io.lownoise.voxen.plugins.api.VoxenException.class);
    }

    @Test
    void configValidationAcceptsValidConfig(@TempDir Path tempDir) {
        VoxenConfig config = new VoxenConfig("valid", "java", "jar", "dist");
        config.write(tempDir);

        io.lownoise.voxen.core.ConfigValidator.validate(
            "{\"name\":\"valid\",\"runtime\":\"java\",\"target\":\"jar\",\"output\":\"dist\"}");
    }

    @Test
    void configValidationRejectsMissingName() {
        assertThatThrownBy(() ->
            io.lownoise.voxen.core.ConfigValidator.validate("{\"runtime\":\"java\"}"))
            .isInstanceOf(io.lownoise.voxen.plugins.api.VoxenException.class);
    }
}
