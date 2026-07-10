package io.lownoise.voxen.testing;

import io.lownoise.voxen.core.VoxenConfig;
import io.lownoise.voxen.plugins.api.VoxenException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

class ConfigFuzzingTest {

    @Test
    void rejectsEmptyJson(@TempDir Path tempDir) throws Exception {
        writeConfig(tempDir, "");
        assertThatThrownBy(() -> VoxenConfig.fromProjectDir(tempDir))
            .isInstanceOf(VoxenException.class);
    }

    @Test
    void rejectsNullJson(@TempDir Path tempDir) throws Exception {
        writeConfig(tempDir, "null");
        assertThatThrownBy(() -> VoxenConfig.fromProjectDir(tempDir))
            .isInstanceOf(VoxenException.class);
    }

    @Test
    void rejectsBooleanJson(@TempDir Path tempDir) throws Exception {
        writeConfig(tempDir, "true");
        assertThatThrownBy(() -> VoxenConfig.fromProjectDir(tempDir))
            .isInstanceOf(VoxenException.class);
    }

    @Test
    void rejectsArrayInsteadOfObject(@TempDir Path tempDir) throws Exception {
        writeConfig(tempDir, "[]");
        assertThatThrownBy(() -> VoxenConfig.fromProjectDir(tempDir))
            .isInstanceOf(VoxenException.class);
    }

    @Test
    void rejectsMissingName(@TempDir Path tempDir) throws Exception {
        writeConfig(tempDir, "{\"runtime\":\"java\"}");
        assertThatThrownBy(() -> VoxenConfig.fromProjectDir(tempDir))
            .isInstanceOf(VoxenException.class)
            .hasMessageContaining("validation");
    }

    @Test
    void rejectsMissingRuntime(@TempDir Path tempDir) throws Exception {
        writeConfig(tempDir, "{\"name\":\"test\"}");
        assertThatThrownBy(() -> VoxenConfig.fromProjectDir(tempDir))
            .isInstanceOf(VoxenException.class)
            .hasMessageContaining("validation");
    }

    @Test
    void rejectsEmptyName(@TempDir Path tempDir) throws Exception {
        writeConfig(tempDir, "{\"name\":\"\",\"runtime\":\"java\"}");
        assertThatThrownBy(() -> VoxenConfig.fromProjectDir(tempDir))
            .isInstanceOf(VoxenException.class)
            .hasMessageContaining("validation");
    }

    @Test
    void rejectsNameWithSpecialCharacters(@TempDir Path tempDir) throws Exception {
        writeConfig(tempDir, "{\"name\":\"../../etc/passwd\",\"runtime\":\"java\"}");
        assertThatThrownBy(() -> VoxenConfig.fromProjectDir(tempDir))
            .isInstanceOf(VoxenException.class)
            .hasMessageContaining("validation");
    }

    @Test
    void rejectsAdditionalProperties(@TempDir Path tempDir) throws Exception {
        writeConfig(tempDir, "{\"name\":\"test\",\"runtime\":\"java\",\"unknownField\":\"value\"}");
        assertThatThrownBy(() -> VoxenConfig.fromProjectDir(tempDir))
            .isInstanceOf(VoxenException.class)
            .hasMessageContaining("validation");
    }

    @Test
    void rejectsNumericName(@TempDir Path tempDir) throws Exception {
        writeConfig(tempDir, "{\"name\":123,\"runtime\":\"java\"}");
        assertThatThrownBy(() -> VoxenConfig.fromProjectDir(tempDir))
            .isInstanceOf(VoxenException.class);
    }

    @Test
    void rejectsNullField(@TempDir Path tempDir) throws Exception {
        writeConfig(tempDir, "{\"name\":\"test\",\"runtime\":null}");
        assertThatThrownBy(() -> VoxenConfig.fromProjectDir(tempDir))
            .isInstanceOf(VoxenException.class)
            .hasMessageContaining("validation");
    }

    @Test
    void acceptsValidConfig(@TempDir Path tempDir) throws Exception {
        writeConfig(tempDir, "{\"name\":\"valid-project\",\"runtime\":\"java\",\"target\":\"jar\",\"output\":\"dist\"}");
        VoxenConfig config = VoxenConfig.fromProjectDir(tempDir);
        assertThat(config.name()).isEqualTo("valid-project");
        assertThat(config.runtime()).isEqualTo("java");
    }

    @Test
    void rejectsInvalidJsonSyntax(@TempDir Path tempDir) throws Exception {
        writeConfig(tempDir, "{\"name\":\"test\",broken");
        assertThatThrownBy(() -> VoxenConfig.fromProjectDir(tempDir))
            .isInstanceOf(VoxenException.class);
    }

    @Test
    void rejectsLongName(@TempDir Path tempDir) throws Exception {
        String longName = "a".repeat(200);
        writeConfig(tempDir, "{\"name\":\"" + longName + "\",\"runtime\":\"java\"}");
        assertThatThrownBy(() -> VoxenConfig.fromProjectDir(tempDir))
            .isInstanceOf(VoxenException.class)
            .hasMessageContaining("validation");
    }

    @Test
    void rejectsDeeplyNestedJson(@TempDir Path tempDir) throws Exception {
        writeConfig(tempDir, "{\"name\":\"test\",\"runtime\":{\"nested\":{\"deeply\":true}}}");
        assertThatThrownBy(() -> VoxenConfig.fromProjectDir(tempDir))
            .isInstanceOf(VoxenException.class)
            .hasMessageContaining("validation");
    }

    @Test
    void rejectsRuntimeWithWhitespace(@TempDir Path tempDir) throws Exception {
        writeConfig(tempDir, "{\"name\":\"test\",\"runtime\":\"java script\"}");
        VoxenConfig config = VoxenConfig.fromProjectDir(tempDir);
        assertThat(config.runtime()).isEqualTo("java script");
    }

    private void writeConfig(Path dir, String content) throws Exception {
        Files.writeString(dir.resolve("voxen.json"), content);
    }
}
