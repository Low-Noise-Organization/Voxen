package io.lownoise.voxen.plugins.api;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class VoxenExceptionTest {

    @Test
    void shouldStoreUserMessageAndHint() {
        VoxenException ex = new VoxenException("Build failed", "Check your source code");
        assertThat(ex.userMessage()).isEqualTo("Build failed");
        assertThat(ex.hint()).isEqualTo("Check your source code");
        assertThat(ex.getMessage()).contains("Build failed");
        assertThat(ex.getMessage()).contains("Check your source code");
    }

    @Test
    void shouldWrapCause() {
        Throwable cause = new RuntimeException("root cause");
        VoxenException ex = new VoxenException("Error", "Check config", cause);
        assertThat(ex.getCause()).isSameAs(cause);
    }
}
