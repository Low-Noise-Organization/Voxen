package io.lownoise.voxen.cli.command;

import io.lownoise.voxen.cli.OutputFormatter;
import io.lownoise.voxen.core.AuditLogger;
import io.lownoise.voxen.core.ConsoleProgress;
import io.lownoise.voxen.core.PluginLoader;
import io.lownoise.voxen.core.ProfileConfig;
import io.lownoise.voxen.core.Project;
import io.lownoise.voxen.core.TelemetryCollector;
import io.lownoise.voxen.plugins.api.PluginContext;
import io.lownoise.voxen.plugins.api.PackageResult;
import picocli.CommandLine;

import java.nio.file.Path;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "package", description = "Package the project for distribution")
public class PackageCommand implements Callable<Integer> {

    @CommandLine.ParentCommand
    io.lownoise.voxen.cli.VoxenCli parent;

    @CommandLine.Parameters(paramLabel = "directory",
        description = "Project directory (default: current directory)", arity = "0..1")
    Path directory;

    @Override
    public Integer call() {
        OutputFormatter fmt = new OutputFormatter(parent != null && parent.json);
        boolean json = parent != null && parent.json;
        Path dir = directory != null ? directory : Path.of(".");
        AuditLogger audit = new AuditLogger(dir);
        ConsoleProgress progress = new ConsoleProgress(System.out, !json);
        TelemetryCollector telemetry = new TelemetryCollector(false);

        var plugins = PluginLoader.loadAll();
        var context = createContext(dir);
        Project project = Project.detect(dir, plugins, context);

        if (parent != null && parent.profile != null) {
            try {
                ProfileConfig profile = ProfileConfig.load(dir, parent.profile);
                fmt.verbose(parent.verbose, "Using profile: " + profile.name());
            } catch (Exception e) {
                fmt.verbose(parent.verbose, "Profile not found, using defaults");
            }
        }

        progress.showSpinner("Packaging " + project.name() + "...", json);
        String opId = "package-" + System.currentTimeMillis();
        telemetry.startOperation(opId, "package", project.name());

        PackageResult result = project.plugin().packageArtifact(context);

        telemetry.finishOperation(opId, result.success(),
            result.success() ? "format=" + result.format() : null);
        audit.packageOp(project.name(), result.success(),
            result.success() ? result.format() : null);

        if (result.success()) {
            progress.done("Package created: " + result.packagePath().getFileName(), json);
            fmt.result(new OutputFormatter.PackageJsonResult(
                "success", project.name(),
                result.packagePath().toString(), result.format(), result.sizeBytes()));
            fmt.log("Package created: " + result.packagePath());
            fmt.log("Format: " + result.format());
            fmt.log("Size: " + formatSize(result.sizeBytes()));
            return 0;
        } else {
            progress.fail("Packaging failed", json);
            fmt.result(new OutputFormatter.PackageJsonResult(
                "failure", project.name(), "", "", 0));
            fmt.error("Packaging failed: " + result.output());
            return 1;
        }
    }

    private PluginContext createContext(Path dir) {
        return new PluginContext() {
            @Override public Path workingDirectory() { return dir; }
            @Override public Path outputDirectory() { return dir.resolve("dist"); }
            @Override public boolean verbose() { return parent != null && parent.verbose; }
            @Override public boolean debug() { return parent != null && parent.debug; }
            @Override public void log(String message) { System.out.println(message); }
            @Override public void verbose(String message) { if (verbose()) System.out.println(message); }
            @Override public void debug(String message) { if (debug()) System.out.println(message); }
            @Override public void error(String message) { System.err.println(message); }
        };
    }

    private static String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }
}
