package io.lownoise.voxen.runtime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

class DotNetRuntimePluginTest {

    private final DotNetRuntimePlugin plugin = new DotNetRuntimePlugin();

    @Test
    void runtimeReturnsDotnet() {
        assertThat(plugin.runtime()).isEqualTo("dotnet");
    }

    @Test
    void detectReturnsTrueForCsproj(@TempDir Path dir) throws Exception {
        Files.createFile(dir.resolve("MyApp.csproj"));
        assertThat(plugin.detect(dir)).isTrue();
    }

    @Test
    void detectReturnsTrueForFsproj(@TempDir Path dir) throws Exception {
        Files.createFile(dir.resolve("MyApp.fsproj"));
        assertThat(plugin.detect(dir)).isTrue();
    }

    @Test
    void detectReturnsFalseForEmptyDir(@TempDir Path dir) {
        assertThat(plugin.detect(dir)).isFalse();
    }

    @Test
    void buildCommand() {
        assertThat(plugin.buildCommand(Path.of(".")))
            .isEqualTo("dotnet publish -c Release -o dist");
    }

    @Test
    void defaultArtifactPattern() {
        assertThat(plugin.defaultArtifactPattern()).isEqualTo("dist/*.dll");
    }

    @Test
    void buildImage() {
        assertThat(plugin.buildImage()).contains("microsoft.com/dotnet/sdk");
    }
}
