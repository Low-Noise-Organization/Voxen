package io.lownoise.voxen.core;

import io.lownoise.voxen.plugins.api.VoxenException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ConfigValidatorTest {

    @Test
    void shouldAcceptValidConfig() {
        ConfigValidator.validate("{\"name\":\"test\",\"runtime\":\"java\"}");
    }

    @Test
    void shouldAcceptFullConfig() {
        ConfigValidator.validate("{\"name\":\"test\",\"runtime\":\"java\",\"target\":\"jar\",\"output\":\"dist\"}");
    }

    @Test
    void shouldRejectMissingName() {
        assertThatThrownBy(() -> ConfigValidator.validate("{\"runtime\":\"java\"}"))
            .isInstanceOf(VoxenException.class)
            .hasMessageContaining("validation");
    }

    @Test
    void shouldRejectMissingRuntime() {
        assertThatThrownBy(() -> ConfigValidator.validate("{\"name\":\"test\"}"))
            .isInstanceOf(VoxenException.class)
            .hasMessageContaining("validation");
    }

    @Test
    void shouldRejectEmptyName() {
        assertThatThrownBy(() -> ConfigValidator.validate("{\"name\":\"\",\"runtime\":\"java\"}"))
            .isInstanceOf(VoxenException.class)
            .hasMessageContaining("validation");
    }

    @Test
    void shouldRejectAdditionalProperties() {
        assertThatThrownBy(() -> ConfigValidator.validate("{\"name\":\"test\",\"runtime\":\"java\",\"extra\":\"value\"}"))
            .isInstanceOf(VoxenException.class)
            .hasMessageContaining("validation");
    }

    @Test
    void shouldRejectNullValue() {
        assertThatThrownBy(() -> ConfigValidator.validate("{\"name\":null,\"runtime\":\"java\"}"))
            .isInstanceOf(VoxenException.class)
            .hasMessageContaining("validation");
    }

    @Test
    void shouldRejectInvalidJson() {
        assertThatThrownBy(() -> ConfigValidator.validate("not json"))
            .isInstanceOf(VoxenException.class);
    }
}
