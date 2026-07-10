package io.lownoise.voxen.core;

import io.lownoise.voxen.plugins.api.Plugin;
import io.lownoise.voxen.plugins.api.VoxenException;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public class PluginLoader {

    private static final String REQUIRED_API_VERSION = "1.0";

    public static List<Plugin> loadAll() {
        List<Plugin> plugins = new ArrayList<>();
        List<String> languages = new ArrayList<>();

        for (Plugin plugin : ServiceLoader.load(Plugin.class)) {
            String apiVersion = plugin.apiVersion();
            if (!isCompatible(apiVersion)) {
                throw new VoxenException(
                    "Plugin '" + plugin.name() + "' requires API version " + apiVersion
                        + " but Voxen supports " + REQUIRED_API_VERSION,
                    "Update the plugin or upgrade Voxen."
                );
            }

            if (languages.contains(plugin.language().toLowerCase())) {
                throw new VoxenException(
                    "Duplicate plugin detected for language: " + plugin.language(),
                    "Remove one of the conflicting plugins from the classpath."
                );
            }

            languages.add(plugin.language().toLowerCase());
            plugins.add(plugin);
        }

        return List.copyOf(plugins);
    }

    private static boolean isCompatible(String pluginVersion) {
        return REQUIRED_API_VERSION.equals(pluginVersion);
    }
}
