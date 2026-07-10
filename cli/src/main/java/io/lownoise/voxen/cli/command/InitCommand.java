package io.lownoise.voxen.cli.command;

import io.lownoise.voxen.cli.OutputFormatter;
import io.lownoise.voxen.core.AuditLogger;
import io.lownoise.voxen.core.ProfileConfig;
import io.lownoise.voxen.core.VoxenConfig;
import picocli.CommandLine;

import java.nio.file.Path;
import java.util.concurrent.Callable;

@CommandLine.Command(
    name = "init",
    description = "Initialize a new Voxen project"
)
public class InitCommand implements Callable<Integer> {

    @CommandLine.ParentCommand
    io.lownoise.voxen.cli.VoxenCli parent;

    @CommandLine.Option(
        names = {"-n", "--name"},
        description = "Project name"
    )
    String name;

    @CommandLine.Option(
        names = {"-r", "--runtime"},
        description = "Runtime (e.g. java, kotlin, rust, node, python, go, dotnet)",
        defaultValue = "java"
    )
    String runtime;

    @CommandLine.Option(
        names = {"-t", "--target"},
        description = "Target output format"
    )
    String target;

    @CommandLine.Option(
        names = {"-o", "--output"},
        description = "Output directory"
    )
    String output;

    @CommandLine.Parameters(
        paramLabel = "directory",
        description = "Project directory (default: current directory)",
        arity = "0..1"
    )
    Path directory;

    @Override
    public Integer call() {
        OutputFormatter fmt = new OutputFormatter(parent != null && parent.json);
        Path dir = directory != null ? directory : Path.of(".");
        AuditLogger audit = new AuditLogger(dir);

        if (dir.resolve("voxen.json").toFile().exists()) {
            fmt.error("Error: voxen.json already exists in " + dir.toAbsolutePath());
            fmt.error("Hint: A Voxen project is already initialized here.");
            return 1;
        }

        String projectName = name != null ? name : dir.toAbsolutePath().getFileName().toString();

        if (parent != null && parent.profile != null) {
            try {
                ProfileConfig profile = ProfileConfig.load(dir, parent.profile);
                fmt.verbose(parent.verbose, "Using profile: " + profile.name());
            } catch (Exception e) {
                fmt.verbose(parent.verbose, "Profile not found, using defaults");
            }
        }

        VoxenConfig config = new VoxenConfig(projectName, runtime, target, output);
        config.write(dir);

        audit.init(projectName, runtime);

        fmt.result(new OutputFormatter.InitJsonResult(
            "success", projectName, runtime, dir.resolve("voxen.json").toAbsolutePath().toString()));
        fmt.log("Created voxen.json in " + dir.toAbsolutePath());
        fmt.log("Project: " + projectName);
        fmt.log("Runtime: " + runtime);
        return 0;
    }
}
