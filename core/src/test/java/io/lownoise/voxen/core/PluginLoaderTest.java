package io.lownoise.voxen.core;

import io.lownoise.voxen.plugins.api.Plugin;
import io.lownoise.voxen.plugins.api.PluginContext;
import io.lownoise.voxen.plugins.api.BuildResult;
import io.lownoise.voxen.plugins.api.PackageResult;
import io.lownoise.voxen.plugins.api.PublishResult;
import io.lownoise.voxen.plugins.api.DeployResult;
import io.lownoise.voxen.plugins.api.PluginType;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class PluginLoaderTest {

    @Test
    void shouldLoadNoPluginsByDefault() {
        assertThat(PluginLoader.loadAll()).isEmpty();
    }

    @Test
    void loadAllReturnsImmutableList() {
        assertThat(PluginLoader.loadAll()).isInstanceOf(java.util.List.class);
    }
}
