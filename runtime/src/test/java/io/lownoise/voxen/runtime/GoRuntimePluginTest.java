package io.lownoise.voxen.runtime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

class GoRuntimePluginTest {

    private final GoRuntimePlugin plugin = new GoRuntimePlugin();

    @Test
    void runtimeReturnsGo() {
        assertThat(plugin.runtime()).isEqualTo("go");
    }

    @Test
    void detectReturnsTrueForGoMod(@TempDir Path dir) throws Exception {
        Files.createFile(dir.resolve("go.mod"));
        assertThat(plugin.detect(dir)).isTrue();
    }

    @Test
    void detectReturnsFalseForEmptyDir(@TempDir Path dir) {
        assertThat(plugin.detect(dir)).isFalse();
    }

    @Test
    void buildCommand() {
        assertThat(plugin.buildCommand(Path.of("."))).isEqualTo("go build -o dist/ ./...");
    }

    @Test
    void defaultArtifactPattern() {
        assertThat(plugin.defaultArtifactPattern()).isEqualTo("dist/*");
    }

    @Test
    void buildImage() {
        assertThat(plugin.buildImage()).contains("golang");
    }
}
