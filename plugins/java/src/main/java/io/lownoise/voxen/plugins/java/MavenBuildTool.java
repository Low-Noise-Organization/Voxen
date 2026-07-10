package io.lownoise.voxen.plugins.java;

import io.lownoise.voxen.plugins.api.BuildResult;
import io.lownoise.voxen.plugins.api.PackageResult;
import io.lownoise.voxen.plugins.api.PublishResult;
import io.lownoise.voxen.plugins.api.DeployResult;
import io.lownoise.voxen.plugins.api.PluginContext;

import java.nio.file.Path;
import java.nio.file.Files;
import java.util.List;

class MavenBuildTool implements BuildTool {

    private static final String TOOL_NAME = "mvn";

    @Override
    public boolean accepts(Path projectDir) {
        return Files.exists(projectDir.resolve("pom.xml"));
    }

    @Override
    public BuildResult build(Path projectDir, PluginContext context) {
        context.log("Maven: running compile...");
        long start = System.currentTimeMillis();
        int exitCode = runMaven(projectDir, List.of("compile"), context);
        long duration = System.currentTimeMillis() - start;

        if (exitCode != 0) {
            return BuildResult.failure("Maven build failed with exit code " + exitCode);
        }

        Path targetDir = projectDir.resolve("target");
        List<Path> artifacts;
        try (var files = Files.walk(targetDir).filter(p -> p.toString().endsWith(".jar") || p.toString().endsWith(".class"))) {
            artifacts = files.limit(50).map(Path::toAbsolutePath).toList();
        } catch (Exception e) {
            artifacts = List.of();
        }

        return BuildResult.success(artifacts, duration, "Maven compile completed");
    }

    @Override
    public PackageResult packageArtifact(Path projectDir, PluginContext context) {
        context.log("Maven: running package...");
        long start = System.currentTimeMillis();
        int exitCode = runMaven(projectDir, List.of("package", "-DskipTests"), context);
        long duration = System.currentTimeMillis() - start;

        if (exitCode != 0) {
            return PackageResult.failure("Maven package failed with exit code " + exitCode);
        }

        Path jarFile = findJar(projectDir);
        if (jarFile == null) {
            return PackageResult.failure("No JAR file found in target/");
        }

        long size;
        try { size = Files.size(jarFile); } catch (Exception e) { size = 0; }

        return PackageResult.success(jarFile, "jar", size,
            "Maven package completed in " + duration + "ms");
    }

    @Override
    public PublishResult publish(Path projectDir, PluginContext context) {
        context.log("Maven: running deploy...");
        int exitCode = runMaven(projectDir, List.of("deploy", "-DskipTests"), context);

        if (exitCode != 0) {
            return PublishResult.failure("Maven deploy failed with exit code " + exitCode);
        }

        return PublishResult.success("maven-repository", null,
            "Artifact published to Maven repository");
    }

    @Override
    public DeployResult deploy(Path projectDir, PluginContext context) {
        context.log("Maven: not yet configured for deployment");
        return DeployResult.success("local", null,
            "Deployment target not configured. Artifacts are available in target/");
    }

    private int runMaven(Path projectDir, List<String> goals, PluginContext context) {
        try {
            var command = new java.util.ArrayList<String>();
            command.add(TOOL_NAME);
            command.addAll(goals);
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
                context.error("Failed to run Maven: " + e.getMessage());
            }
            return -1;
        }
    }

    private Path findJar(Path projectDir) {
        Path targetDir = projectDir.resolve("target");
        if (!Files.exists(targetDir)) {
            return null;
        }
        try (var files = Files.walk(targetDir)) {
            return files
                .filter(p -> p.toString().endsWith(".jar") && !p.toString().endsWith("-sources.jar") && !p.toString().endsWith("-javadoc.jar"))
                .findFirst()
                .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }
}
