package io.lownoise.voxen.cli.command;

import io.lownoise.voxen.cli.OutputFormatter;
import io.lownoise.voxen.core.AuditLogger;
import io.lownoise.voxen.core.ConsoleProgress;
import io.lownoise.voxen.core.PluginLoader;
import io.lownoise.voxen.core.ProfileConfig;
import io.lownoise.voxen.core.Project;
import io.lownoise.voxen.core.TelemetryCollector;
import io.lownoise.voxen.plugins.api.PluginContext;
import io.lownoise.voxen.plugins.api.DeployResult;
import picocli.CommandLine;

import java.nio.file.Path;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "deploy", description = "Deploy the project to an environment")
public class DeployCommand implements Callable<Integer> {

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

        progress.showSpinner("Deploying " + project.name() + "...", json);
        String opId = "deploy-" + System.currentTimeMillis();
        telemetry.startOperation(opId, "deploy", project.name());

        DeployResult result = project.plugin().deploy(context);

        telemetry.finishOperation(opId, result.success(),
            result.success() ? "environment=" + result.targetEnvironment() : null);
        audit.deploy(project.name(), result.success(), result.targetEnvironment());

        if (result.success()) {
            progress.done("Deployed successfully", json);
            fmt.result(new OutputFormatter.DeployJsonResult(
                "success", project.name(), result.targetEnvironment(), result.deploymentUrl()));
            fmt.log("Deployed to: " + result.targetEnvironment());
            if (result.deploymentUrl() != null) {
                fmt.log("URL: " + result.deploymentUrl());
            }
            return 0;
        } else {
            progress.fail("Deployment failed", json);
            fmt.result(new OutputFormatter.DeployJsonResult(
                "failure", project.name(), "", null));
            fmt.error("Deployment failed: " + result.output());
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
}
