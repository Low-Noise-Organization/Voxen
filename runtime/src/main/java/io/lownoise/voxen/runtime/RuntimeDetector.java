package io.lownoise.voxen.runtime;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RuntimeDetector {

    private static final List<RuntimePlugin> PLUGINS = List.of(
        new JavaRuntimePlugin(),
        new NodeRuntimePlugin(),
        new PythonRuntimePlugin(),
        new GoRuntimePlugin(),
        new RustRuntimePlugin(),
        new DotNetRuntimePlugin()
    );

    public Optional<String> detectJavaHome() {
        String javaHome = System.getenv("JAVA_HOME");
        if (javaHome != null && !javaHome.isBlank()) {
            Path javac = Paths.get(javaHome, "bin", "javac");
            if (Files.exists(javac)) {
                return Optional.of(javaHome);
            }
        }

        String javaHomeAlt = System.getProperty("java.home");
        if (javaHomeAlt != null) {
            Path javac = Paths.get(javaHomeAlt, "bin", "javac");
            if (Files.exists(javac)) {
                return Optional.of(javaHomeAlt);
            }
            Path javacParent = Paths.get(javaHomeAlt).getParent();
            if (javacParent != null) {
                javac = javacParent.resolve("bin").resolve("javac");
                if (Files.exists(javac)) {
                    return Optional.of(javacParent.toString());
                }
            }
        }

        return Optional.empty();
    }

    public String javaVersion() {
        return System.getProperty("java.version", "unknown");
    }

    public boolean isJavaAvailable() {
        return detectJavaHome().isPresent();
    }

    public boolean isMavenAvailable() {
        return commandAvailable("mvn");
    }

    public boolean isGradleAvailable() {
        return commandAvailable("gradle");
    }

    public Optional<RuntimePlugin> detectRuntime(Path projectDir) {
        return PLUGINS.stream()
            .filter(p -> p.detect(projectDir))
            .findFirst();
    }

    public List<RuntimePlugin> availablePlugins() {
        return PLUGINS;
    }

    public List<String> supportedRuntimes() {
        return PLUGINS.stream().map(RuntimePlugin::runtime).toList();
    }

    public boolean isRuntimeInstalled(String runtime) {
        return switch (runtime.toLowerCase()) {
            case "java" -> isJavaAvailable();
            case "node" -> commandAvailable("node");
            case "python" -> commandAvailable("python3") || commandAvailable("python");
            case "go" -> commandAvailable("go");
            case "rust" -> commandAvailable("cargo");
            case "dotnet" -> commandAvailable("dotnet");
            default -> false;
        };
    }

    private boolean commandAvailable(String name) {
        try {
            ProcessBuilder pb = new ProcessBuilder(name, "--version")
                .inheritIO()
                .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                .redirectError(ProcessBuilder.Redirect.DISCARD);
            Process p = pb.start();
            int exit = p.waitFor();
            return exit == 0;
        } catch (Exception e) {
            return false;
        }
    }
}
