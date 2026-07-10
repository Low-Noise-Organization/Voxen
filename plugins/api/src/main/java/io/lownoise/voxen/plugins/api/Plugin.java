package io.lownoise.voxen.plugins.api;

import java.util.Set;

public interface Plugin {

    String name();

    String language();

    Set<PluginType> supportedTypes();

    default String apiVersion() {
        return "1.0";
    }

    void init(PluginContext context);

    BuildResult build(PluginContext context);

    PackageResult packageArtifact(PluginContext context);

    PublishResult publish(PluginContext context);

    DeployResult deploy(PluginContext context);
}
