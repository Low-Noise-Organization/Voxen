package io.lownoise.voxen.runtime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

class NodeRuntimePluginTest {

    private final NodeRuntimePlugin plugin = new NodeRuntimePlugin();

    @Test
    void runtimeReturnsNode() {
        assertThat(plugin.runtime()).isEqualTo("node");
    }

    @Test
    void detectReturnsTrueForPackageJson(@TempDir Path dir) throws Exception {
        Files.createFile(dir.resolve("package.json"));
        assertThat(plugin.detect(dir)).isTrue();
    }

    @Test
    void detectReturnsFalseForEmptyDir(@TempDir Path dir) {
        assertThat(plugin.detect(dir)).isFalse();
    }

    @Test
    void buildCommand() {
        assertThat(plugin.buildCommand(Path.of("."))).isEqualTo("npm run build");
    }

    @Test
    void defaultArtifactPattern() {
        assertThat(plugin.defaultArtifactPattern()).isEqualTo("dist/*.zip");
    }

    @Test
    void buildImage() {
        assertThat(plugin.buildImage()).contains("node");
    }
}
