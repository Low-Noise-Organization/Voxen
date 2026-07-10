package io.lownoise.voxen.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

class ProfileConfigTest {

    @Test
    void builtinDevProfileExists() {
        ProfileConfig dev = ProfileConfig.builtin("dev");
        assertThat(dev.name()).isEqualTo("dev");
        assertThat(dev.effectiveVariables()).containsEntry("env", "development");
        assertThat(dev.effectiveSettings()).containsEntry("verbose", "true");
    }

    @Test
    void builtinStagingProfileExists() {
        ProfileConfig staging = ProfileConfig.builtin("staging");
        assertThat(staging.name()).isEqualTo("staging");
        assertThat(staging.effectiveVariables()).containsEntry("env", "staging");
        assertThat(staging.effectiveSettings()).containsEntry("verbose", "false");
    }

    @Test
    void builtinProdProfileExists() {
        ProfileConfig prod = ProfileConfig.builtin("prod");
        assertThat(prod.name()).isEqualTo("prod");
        assertThat(prod.effectiveVariables()).containsEntry("env", "production");
        assertThat(prod.effectiveSettings()).containsEntry("skipTests", "true");
    }

    @Test
    void builtinUnknownProfileThrows() {
        assertThatThrownBy(() -> ProfileConfig.builtin("unknown"))
            .isInstanceOf(io.lownoise.voxen.plugins.api.VoxenException.class);
    }

    @Test
    void loadBuiltinByNameReturnsIt() {
        ProfileConfig profile = ProfileConfig.load(Path.of("."), "dev");
        assertThat(profile.name()).isEqualTo("dev");
    }

    @Test
    void loadCustomProfileFromFile(@TempDir Path tempDir) throws Exception {
        String json = "{\"name\":\"custom\",\"variables\":{\"key\":\"val\"},\"settings\":{\"opt\":\"on\"}}";
        java.nio.file.Files.writeString(tempDir.resolve("voxen-custom.json"), json);

        ProfileConfig profile = ProfileConfig.load(tempDir, "custom");
        assertThat(profile.name()).isEqualTo("custom");
        assertThat(profile.effectiveVariables()).containsEntry("key", "val");
        assertThat(profile.effectiveSettings()).containsEntry("opt", "on");
    }

    @Test
    void loadNonexistentCustomProfileThrows(@TempDir Path tempDir) {
        assertThatThrownBy(() -> ProfileConfig.load(tempDir, "nonexistent"))
            .isInstanceOf(io.lownoise.voxen.plugins.api.VoxenException.class);
    }

    @Test
    void profileWithNullVariablesReturnsEmpty() {
        ProfileConfig profile = new ProfileConfig("test", null, null, null);
        assertThat(profile.effectiveVariables()).isEmpty();
        assertThat(profile.effectiveSettings()).isEmpty();
    }
}
