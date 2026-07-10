package io.lownoise.voxen.plugins.java;

import io.lownoise.voxen.plugins.api.BuildResult;
import io.lownoise.voxen.plugins.api.PackageResult;
import io.lownoise.voxen.plugins.api.PublishResult;
import io.lownoise.voxen.plugins.api.DeployResult;
import io.lownoise.voxen.plugins.api.PluginContext;

import java.nio.file.Path;
import java.nio.file.Files;
import java.util.List;

class GradleBuildTool implements BuildTool {

    private static final String TOOL_NAME = "gradle";

    @Override
    public boolean accepts(Path projectDir) {
        return Files.exists(projectDir.resolve("build.gradle"))
            || Files.exists(projectDir.resolve("build.gradle.kts"));
    }

    @Override
    public BuildResult build(Path projectDir, PluginContext context) {
        context.log("Gradle: running classes...");
        long start = System.currentTimeMillis();
        int exitCode = runGradle(projectDir, List.of("classes"), context);
        long duration = System.currentTimeMillis() - start;

        if (exitCode != 0) {
            return BuildResult.failure("Gradle build failed with exit code " + exitCode);
        }

        Path buildDir = projectDir.resolve("build");
        List<Path> artifacts;
        try (var files = Files.walk(buildDir).filter(p -> p.toString().endsWith(".jar") || p.toString().endsWith(".class"))) {
            artifacts = files.limit(50).map(Path::toAbsolutePath).toList();
        } catch (Exception e) {
            artifacts = List.of();
        }

        return BuildResult.success(artifacts, duration, "Gradle classes completed");
    }

    @Override
    public PackageResult packageArtifact(Path projectDir, PluginContext context) {
        context.log("Gradle: running jar...");
        long start = System.currentTimeMillis();
        int exitCode = runGradle(projectDir, List.of("jar"), context);
        long duration = System.currentTimeMillis() - start;

        if (exitCode != 0) {
            return PackageResult.failure("Gradle jar failed with exit code " + exitCode);
        }

        Path jarFile = findJar(projectDir);
        if (jarFile == null) {
            return PackageResult.failure("No JAR file found in build/libs/");
        }

        long size;
        try { size = Files.size(jarFile); } catch (Exception e) { size = 0; }

        return PackageResult.success(jarFile, "jar", size,
            "Gradle jar completed in " + duration + "ms");
    }

    @Override
    public PublishResult publish(Path projectDir, PluginContext context) {
        context.log("Gradle: running publish...");
        int exitCode = runGradle(projectDir, List.of("publish"), context);

        if (exitCode != 0) {
            return PublishResult.failure("Gradle publish failed with exit code " + exitCode);
        }

        return PublishResult.success("gradle-repository", null,
            "Artifact published to Gradle repository");
    }

    @Override
    public DeployResult deploy(Path projectDir, PluginContext context) {
        context.log("Gradle: not yet configured for deployment");
        return DeployResult.success("local", null,
            "Deployment target not configured. Artifacts are available in build/libs/");
    }

    private int runGradle(Path projectDir, List<String> tasks, PluginContext context) {
        try {
            var command = new java.util.ArrayList<String>();
            String gradleCmd = findGradleCommand(projectDir);
            command.add(gradleCmd);
            command.addAll(tasks);
            if (context.verbose()) {
                command.add("--verbose");
            }

            ProcessBuilder pb = new ProcessBuilder(command)
                .directory(projectDir.toFile())
                .inheritIO();

            Process process = pb.start();
            return process.waitFor();

        } catch (Exception e) {
            if (context.verbose()) {
                context.error("Failed to run Gradle: " + e.getMessage());
            }
            return -1;
        }
    }

    private String findGradleCommand(Path projectDir) {
        Path gradlew = projectDir.resolve("gradlew");
        if (Files.exists(gradlew)) {
            return gradlew.toAbsolutePath().toString();
        }
        return TOOL_NAME;
    }

    private Path findJar(Path projectDir) {
        Path libsDir = projectDir.resolve("build").resolve("libs");
        if (!Files.exists(libsDir)) {
            return null;
        }
        try (var files = Files.walk(libsDir)) {
            return files
                .filter(p -> p.toString().endsWith(".jar") && !p.toString().endsWith("-sources.jar") && !p.toString().endsWith("-javadoc.jar"))
                .findFirst()
                .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }
}
