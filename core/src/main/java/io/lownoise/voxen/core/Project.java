package io.lownoise.voxen.core;

import io.lownoise.voxen.plugins.api.Plugin;
import io.lownoise.voxen.plugins.api.PluginContext;
import io.lownoise.voxen.plugins.api.VoxenException;

import java.nio.file.Path;
import java.util.List;

public record Project(
    ProjectConfig config,
    Plugin plugin,
    Path directory
) {

    public Project {
        if (config == null || plugin == null || directory == null) {
            throw new VoxenException(
                "Project fields must not be null.",
                "This is an internal error. Report it at https://github.com/lownoise/voxen/issues"
            );
        }
    }

    public static Project detect(Path dir, List<Plugin> availablePlugins, PluginContext context) {
        VoxenConfig config = VoxenConfig.fromProjectDir(dir);

        Plugin plugin = availablePlugins.stream()
            .filter(p -> p.language().equalsIgnoreCase(config.runtime()))
            .findFirst()
            .orElseThrow(() -> new VoxenException(
                "No plugin found for runtime: " + config.runtime(),
                "Supported runtimes: " + availablePlugins.stream().map(Plugin::language).toList()
                    + ". Install a plugin or check your voxen.json runtime field."
            ));

        return new Project(config.toProjectConfig(dir), plugin, dir);
    }

    public String name() {
        return config.name();
    }
}
