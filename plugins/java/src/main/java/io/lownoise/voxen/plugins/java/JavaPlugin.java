package io.lownoise.voxen.plugins.java;

import io.lownoise.voxen.plugins.api.*;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public class JavaPlugin implements Plugin {

    private final List<BuildTool> buildTools;

    public JavaPlugin() {
        this.buildTools = List.of(new MavenBuildTool(), new GradleBuildTool());
    }

    @Override
    public String name() {
        return "java";
    }

    @Override
    public String language() {
        return "java";
    }

    @Override
    public Set<PluginType> supportedTypes() {
        return Set.of(PluginType.BUILD, PluginType.PACKAGE, PluginType.PUBLISH, PluginType.DEPLOY);
    }

    @Override
    public void init(PluginContext context) {
        context.log("Java plugin initialized");
    }

    @Override
    public BuildResult build(PluginContext context) {
        BuildTool tool = detectBuildTool(context.workingDirectory());
        if (tool == null) {
            return BuildResult.failure(
                "No build tool detected.\n" +
                "Expected pom.xml (Maven) or build.gradle/build.gradle.kts (Gradle) in " +
                context.workingDirectory());
        }
        context.verbose("Detected build tool: " + tool.getClass().getSimpleName());
        return tool.build(context.workingDirectory(), context);
    }

    @Override
    public PackageResult packageArtifact(PluginContext context) {
        BuildTool tool = detectBuildTool(context.workingDirectory());
        if (tool == null) {
            return PackageResult.failure(
                "No build tool detected.\n" +
                "Expected pom.xml (Maven) or build.gradle/build.gradle.kts (Gradle) in " +
                context.workingDirectory());
        }
        context.verbose("Detected build tool: " + tool.getClass().getSimpleName());
        return tool.packageArtifact(context.workingDirectory(), context);
    }

    @Override
    public PublishResult publish(PluginContext context) {
        BuildTool tool = detectBuildTool(context.workingDirectory());
        if (tool == null) {
            return PublishResult.failure(
                "No build tool detected.\n" +
                "Expected pom.xml (Maven) or build.gradle/build.gradle.kts (Gradle) in " +
                context.workingDirectory());
        }
        context.verbose("Detected build tool: " + tool.getClass().getSimpleName());
        return tool.publish(context.workingDirectory(), context);
    }

    @Override
    public DeployResult deploy(PluginContext context) {
        BuildTool tool = detectBuildTool(context.workingDirectory());
        if (tool == null) {
            return DeployResult.failure(
                "No build tool detected.\n" +
                "Expected pom.xml (Maven) or build.gradle/build.gradle.kts (Gradle) in " +
                context.workingDirectory());
        }
        context.verbose("Detected build tool: " + tool.getClass().getSimpleName());
        return tool.deploy(context.workingDirectory(), context);
    }

    private BuildTool detectBuildTool(Path projectDir) {
        return buildTools.stream()
            .filter(t -> t.accepts(projectDir))
            .findFirst()
            .orElse(null);
    }
}
