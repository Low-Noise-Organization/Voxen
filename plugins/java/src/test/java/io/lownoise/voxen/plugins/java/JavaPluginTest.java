package io.lownoise.voxen.plugins.java;

import io.lownoise.voxen.plugins.api.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class JavaPluginTest {

    @Test
    void shouldReportJavaLanguage() {
        JavaPlugin plugin = new JavaPlugin();
        assertThat(plugin.name()).isEqualTo("java");
        assertThat(plugin.language()).isEqualTo("java");
    }

    @Test
    void shouldSupportAllPluginTypes() {
        JavaPlugin plugin = new JavaPlugin();
        assertThat(plugin.supportedTypes()).containsExactlyInAnyOrder(
            PluginType.BUILD, PluginType.PACKAGE, PluginType.PUBLISH, PluginType.DEPLOY);
    }

    @Test
    void shouldFailBuildWhenNoProjectFound(@TempDir Path tempDir) {
        JavaPlugin plugin = new JavaPlugin();
        PluginContext ctx = createContext(tempDir);
        BuildResult result = plugin.build(ctx);
        assertThat(result.success()).isFalse();
        assertThat(result.output()).contains("No build tool detected");
    }

    @Test
    void shouldAcceptMavenProject(@TempDir Path tempDir) throws Exception {
        Files.createFile(tempDir.resolve("pom.xml"));
        JavaPlugin plugin = new JavaPlugin();
        PluginContext ctx = createContext(tempDir);
        BuildResult result = plugin.build(ctx);
        assertThat(result.success()).isFalse();
        assertThat(result.output()).doesNotContain("No build tool detected");
    }

    @Test
    void shouldAcceptGradleProject(@TempDir Path tempDir) throws Exception {
        Files.createFile(tempDir.resolve("build.gradle.kts"));
        JavaPlugin plugin = new JavaPlugin();
        PluginContext ctx = createContext(tempDir);
        BuildResult result = plugin.build(ctx);
        assertThat(result.success()).isFalse();
        assertThat(result.output()).doesNotContain("No build tool detected");
    }

    @Test
    void shouldAcceptGradleGroovyProject(@TempDir Path tempDir) throws Exception {
        Files.createFile(tempDir.resolve("build.gradle"));
        JavaPlugin plugin = new JavaPlugin();
        PluginContext ctx = createContext(tempDir);
        BuildResult result = plugin.build(ctx);
        assertThat(result.success()).isFalse();
        assertThat(result.output()).doesNotContain("No build tool detected");
    }

    private PluginContext createContext(Path dir) {
        return new PluginContext() {
            @Override public Path workingDirectory() { return dir; }
            @Override public Path outputDirectory() { return dir.resolve("dist"); }
            @Override public boolean verbose() { return false; }
            @Override public boolean debug() { return false; }
            @Override public void log(String msg) {}
            @Override public void verbose(String msg) {}
            @Override public void debug(String msg) {}
            @Override public void error(String msg) {}
        };
    }
}
