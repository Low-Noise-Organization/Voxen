package io.lownoise.voxen.core;

import io.lownoise.voxen.plugins.api.VoxenException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

class VoxenConfigTest {

    @Test
    void shouldCreateAndReadConfig(@TempDir Path tempDir) {
        VoxenConfig config = new VoxenConfig("test-project", "java", "jar", "dist");
        config.write(tempDir);

        VoxenConfig loaded = VoxenConfig.fromProjectDir(tempDir);
        assertThat(loaded.name()).isEqualTo("test-project");
        assertThat(loaded.runtime()).isEqualTo("java");
        assertThat(loaded.target()).isEqualTo("jar");
        assertThat(loaded.output()).isEqualTo("dist");
    }

    @Test
    void shouldThrowWhenNoConfigFile(@TempDir Path tempDir) {
        assertThatThrownBy(() -> VoxenConfig.fromProjectDir(tempDir))
            .isInstanceOf(VoxenException.class)
            .hasMessageContaining("voxen.json");
    }

    @Test
    void shouldConvertToProjectConfig() {
        VoxenConfig config = new VoxenConfig("test", "java", "jar", "dist");
        ProjectConfig pc = config.toProjectConfig(Path.of("/project"));
        assertThat(pc.name()).isEqualTo("test");
        assertThat(pc.runtime()).isEqualTo("java");
        assertThat(pc.projectDir()).isEqualTo(Path.of("/project"));
    }

    @Test
    void shouldDefaultOutputToDist() {
        VoxenConfig config = new VoxenConfig("test", "java", null, null);
        ProjectConfig pc = config.toProjectConfig(Path.of("/project"));
        assertThat(pc.outputDirectory()).isEqualTo(Path.of("/project/dist"));
    }
}
