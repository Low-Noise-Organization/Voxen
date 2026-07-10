package io.lownoise.voxen.runtime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

class PythonRuntimePluginTest {

    private final PythonRuntimePlugin plugin = new PythonRuntimePlugin();

    @Test
    void runtimeReturnsPython() {
        assertThat(plugin.runtime()).isEqualTo("python");
    }

    @Test
    void detectReturnsTrueForSetupPy(@TempDir Path dir) throws Exception {
        Files.createFile(dir.resolve("setup.py"));
        assertThat(plugin.detect(dir)).isTrue();
    }

    @Test
    void detectReturnsTrueForPyprojectToml(@TempDir Path dir) throws Exception {
        Files.createFile(dir.resolve("pyproject.toml"));
        assertThat(plugin.detect(dir)).isTrue();
    }

    @Test
    void detectReturnsTrueForRequirementsTxt(@TempDir Path dir) throws Exception {
        Files.createFile(dir.resolve("requirements.txt"));
        assertThat(plugin.detect(dir)).isTrue();
    }

    @Test
    void detectReturnsFalseForEmptyDir(@TempDir Path dir) {
        assertThat(plugin.detect(dir)).isFalse();
    }

    @Test
    void buildCommandUsesSetupPyWhenPresent(@TempDir Path dir) throws Exception {
        Files.createFile(dir.resolve("setup.py"));
        assertThat(plugin.buildCommand(dir)).contains("setup.py");
    }

    @Test
    void buildCommandUsesBuildWhenPyprojectTomlPresent(@TempDir Path dir) throws Exception {
        Files.createFile(dir.resolve("pyproject.toml"));
        assertThat(plugin.buildCommand(dir)).contains("python -m build");
    }

    @Test
    void buildCommandDefaultsToPipInstall(@TempDir Path dir) throws Exception {
        Files.createFile(dir.resolve("requirements.txt"));
        assertThat(plugin.buildCommand(dir)).contains("pip install");
    }

    @Test
    void defaultArtifactPattern() {
        assertThat(plugin.defaultArtifactPattern()).isEqualTo("dist/*.whl");
    }

    @Test
    void buildImage() {
        assertThat(plugin.buildImage()).contains("python");
    }
}
