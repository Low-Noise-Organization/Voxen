package io.lownoise.voxen.plugins.java;

import io.lownoise.voxen.plugins.api.BuildResult;
import io.lownoise.voxen.plugins.api.PackageResult;
import io.lownoise.voxen.plugins.api.PublishResult;
import io.lownoise.voxen.plugins.api.DeployResult;
import io.lownoise.voxen.plugins.api.PluginContext;

import java.nio.file.Path;

interface BuildTool {
    boolean accepts(Path projectDir);
    BuildResult build(Path projectDir, PluginContext context);
    PackageResult packageArtifact(Path projectDir, PluginContext context);
    PublishResult publish(Path projectDir, PluginContext context);
    DeployResult deploy(Path projectDir, PluginContext context);
}
