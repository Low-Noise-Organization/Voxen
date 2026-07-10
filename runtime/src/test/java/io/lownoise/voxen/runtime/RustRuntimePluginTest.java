package io.lownoise.voxen.runtime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

class RustRuntimePluginTest {

    private final RustRuntimePlugin plugin = new RustRuntimePlugin();

    @Test
    void runtimeReturnsRust() {
        assertThat(plugin.runtime()).isEqualTo("rust");
    }

    @Test
    void detectReturnsTrueForCargoToml(@TempDir Path dir) throws Exception {
        Files.createFile(dir.resolve("Cargo.toml"));
        assertThat(plugin.detect(dir)).isTrue();
    }

    @Test
    void detectReturnsFalseForEmptyDir(@TempDir Path dir) {
        assertThat(plugin.detect(dir)).isFalse();
    }

    @Test
    void buildCommand() {
        assertThat(plugin.buildCommand(Path.of("."))).isEqualTo("cargo build --release");
    }

    @Test
    void defaultArtifactPattern() {
        assertThat(plugin.defaultArtifactPattern()).isEqualTo("target/release/*");
    }

    @Test
    void buildImage() {
        assertThat(plugin.buildImage()).contains("rust");
    }
}
