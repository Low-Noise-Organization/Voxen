package io.lownoise.voxen.cli.command;

import io.lownoise.voxen.cli.OutputFormatter;
import io.lownoise.voxen.core.PluginLoader;
import io.lownoise.voxen.runtime.RuntimeDetector;
import picocli.CommandLine;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@CommandLine.Command(
    name = "info",
    description = "Display system and project information"
)
public class InfoCommand implements Callable<Integer> {

    @CommandLine.ParentCommand
    io.lownoise.voxen.cli.VoxenCli parent;

    @CommandLine.Parameters(paramLabel = "directory",
        description = "Project directory (default: current directory)", arity = "0..1")
    Path directory;

    @Override
    public Integer call() {
        OutputFormatter fmt = new OutputFormatter(parent != null && parent.json);
        RuntimeDetector detector = new RuntimeDetector();
        Path dir = directory != null ? directory : Path.of(".");

        if (fmt.isJson()) {
            var node = OutputFormatter.object();
            node.put("version", "0.1.0-SNAPSHOT");
            node.put("javaVersion", detector.javaVersion());
            node.put("os", System.getProperty("os.name", "unknown"));
            node.put("osArch", System.getProperty("os.arch", "unknown"));
            node.put("processors", Runtime.getRuntime().availableProcessors());
            node.put("totalMemory", Runtime.getRuntime().totalMemory());
            node.put("freeMemory", Runtime.getRuntime().freeMemory());
            node.put("maxMemory", Runtime.getRuntime().maxMemory());

            var plugins = PluginLoader.loadAll();
            var pluginsNode = OutputFormatter.array();
            for (var p : plugins) {
                pluginsNode.add(OutputFormatter.object()
                    .put("name", p.name())
                    .put("language", p.language())
                    .put("apiVersion", p.apiVersion()));
            }
            node.set("plugins", pluginsNode);

            var projectNode = OutputFormatter.object();
            Path voxenJson = dir.resolve("voxen.json");
            if (voxenJson.toFile().exists()) {
                projectNode.put("status", "initialized");
                projectNode.put("path", dir.toAbsolutePath().toString());
            } else {
                projectNode.put("status", "uninitialized");
            }
            node.set("project", projectNode);

            fmt.result(() -> node);
        } else {
            fmt.log("Voxen 0.1.0-SNAPSHOT");
            fmt.log("");
            fmt.log("System:");
            fmt.log("  Java:    " + detector.javaVersion());
            fmt.log("  OS:      " + System.getProperty("os.name", "unknown")
                + " (" + System.getProperty("os.arch", "unknown") + ")");
            fmt.log("  CPUs:    " + Runtime.getRuntime().availableProcessors());
            fmt.log("  Memory:  "
                + formatBytes(Runtime.getRuntime().totalMemory()) + " total, "
                + formatBytes(Runtime.getRuntime().freeMemory()) + " free");
            fmt.log("");
            fmt.log("Plugins:");
            var plugins = PluginLoader.loadAll();
            if (plugins.isEmpty()) {
                fmt.log("  (none loaded)");
            } else {
                for (var p : plugins) {
                    fmt.log("  " + p.name() + " (" + p.language() + ") v" + p.apiVersion());
                }
            }
            fmt.log("");
            fmt.log("Project:");
            Path voxenJson = dir.resolve("voxen.json");
            if (voxenJson.toFile().exists()) {
                fmt.log("  Status: initialized");
                fmt.log("  Path:   " + dir.toAbsolutePath());
            } else {
                fmt.log("  Status: uninitialized");
                fmt.log("  Path:   " + dir.toAbsolutePath());
                fmt.log("  Hint:   Run 'voxen init' to create a project");
            }
        }
        return 0;
    }

    private static String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024L * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }
}
