package io.lownoise.voxen.core;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

class PluginSandboxTest {

    @Test
    void sandboxIsDisabledByDefault() {
        PluginSandbox sandbox = new PluginSandbox();
        assertThat(sandbox.isEnabled()).isFalse();
    }

    @Test
    void sandboxCanBeEnabled() {
        PluginSandbox sandbox = new PluginSandbox(true);
        assertThat(sandbox.isEnabled()).isTrue();
    }

    @Test
    void sandboxCanBeToggled() {
        PluginSandbox sandbox = new PluginSandbox();
        sandbox.setEnabled(true);
        assertThat(sandbox.isEnabled()).isTrue();
        sandbox.setEnabled(false);
        assertThat(sandbox.isEnabled()).isFalse();
    }

    @Test
    void applyIfEnabledDoesNothingWhenDisabled() {
        PluginSandbox sandbox = new PluginSandbox();
        assertThatCode(() ->
            sandbox.applyIfEnabled("test-plugin", Path.of("/tmp")))
            .doesNotThrowAnyException();
    }

    @Test
    void sandboxCanDisable() {
        PluginSandbox sandbox = new PluginSandbox();
        assertThatCode(() -> sandbox.disable()).doesNotThrowAnyException();
    }
}
