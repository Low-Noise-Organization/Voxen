package io.lownoise.voxen.core;

import io.lownoise.voxen.plugins.api.*;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class ProjectTest {

    @Test
    void shouldRejectNullConfig() {
        assertThatThrownBy(() -> new Project(null, createPlugin(), Path.of(".")))
            .isInstanceOf(VoxenException.class);
    }

    @Test
    void shouldRejectNullPlugin() {
        assertThatThrownBy(() -> new Project(createConfig(), null, Path.of(".")))
            .isInstanceOf(VoxenException.class);
    }

    @Test
    void shouldRejectNullDirectory() {
        assertThatThrownBy(() -> new Project(createConfig(), createPlugin(), null))
            .isInstanceOf(VoxenException.class);
    }

    @Test
    void shouldReturnNameFromConfig() {
        ProjectConfig config = new ProjectConfig("my-project", "java", null, null, Path.of("."));
        Project project = new Project(config, createPlugin(), Path.of("."));
        assertThat(project.name()).isEqualTo("my-project");
    }

    @Test
    void detectShouldFailWhenNoConfig() {
        assertThatThrownBy(() -> Project.detect(Path.of("/nonexistent"), List.of(createPlugin()), createContext()))
            .isInstanceOf(VoxenException.class);
    }

    private Plugin createPlugin() {
        return new Plugin() {
            @Override public String name() { return "test"; }
            @Override public String language() { return "java"; }
            @Override public Set<PluginType> supportedTypes() { return Set.of(PluginType.BUILD); }
            @Override public void init(PluginContext ctx) {}
            @Override public BuildResult build(PluginContext ctx) { return BuildResult.success(List.of(), 0, ""); }
            @Override public PackageResult packageArtifact(PluginContext ctx) { return PackageResult.failure(""); }
            @Override public PublishResult publish(PluginContext ctx) { return PublishResult.failure(""); }
            @Override public DeployResult deploy(PluginContext ctx) { return DeployResult.failure(""); }
        };
    }

    private ProjectConfig createConfig() {
        return new ProjectConfig("test", "java", "jar", "dist", Path.of("."));
    }

    private PluginContext createContext() {
        return new PluginContext() {
            @Override public Path workingDirectory() { return Path.of("."); }
            @Override public Path outputDirectory() { return Path.of("dist"); }
            @Override public boolean verbose() { return false; }
            @Override public boolean debug() { return false; }
            @Override public void log(String msg) {}
            @Override public void verbose(String msg) {}
            @Override public void debug(String msg) {}
            @Override public void error(String msg) {}
        };
    }
}
