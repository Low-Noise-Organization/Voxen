package io.lownoise.voxen.core;

import io.lownoise.voxen.plugins.api.VoxenException;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

class ProjectConfigTest {

    @Test
    void shouldRejectBlankName() {
        assertThatThrownBy(() -> new ProjectConfig("", "java", "jar", "dist", Path.of(".")))
            .isInstanceOf(VoxenException.class);
    }

    @Test
    void shouldRejectBlankRuntime() {
        assertThatThrownBy(() -> new ProjectConfig("test", "", "jar", "dist", Path.of(".")))
            .isInstanceOf(VoxenException.class);
    }

    @Test
    void shouldRejectNullProjectDir() {
        assertThatThrownBy(() -> new ProjectConfig("test", "java", "jar", "dist", null))
            .isInstanceOf(VoxenException.class);
    }

    @Test
    void shouldDefaultOutputDirectory() {
        ProjectConfig config = new ProjectConfig("test", "java", null, null, Path.of("/project"));
        assertThat(config.outputDirectory()).isEqualTo(Path.of("/project/dist"));
    }

    @Test
    void shouldUseCustomOutputDirectory() {
        ProjectConfig config = new ProjectConfig("test", "java", null, "build", Path.of("/project"));
        assertThat(config.outputDirectory()).isEqualTo(Path.of("/project/build"));
    }

    @Test
    void shouldCreateCopyWithNewDir() {
        ProjectConfig config = new ProjectConfig("test", "java", "jar", "dist", Path.of("/project"));
        ProjectConfig copied = config.withProjectDir(Path.of("/other"));
        assertThat(copied.projectDir()).isEqualTo(Path.of("/other"));
        assertThat(copied.name()).isEqualTo("test");
        assertThat(copied.outputDirectory()).isEqualTo(Path.of("/other/dist"));
    }
}
