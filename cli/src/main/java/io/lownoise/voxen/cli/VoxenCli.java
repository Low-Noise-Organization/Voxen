package io.lownoise.voxen.cli;

import io.lownoise.voxen.cli.command.*;
import io.lownoise.voxen.plugins.api.VoxenException;
import picocli.CommandLine;

@CommandLine.Command(
    name = "voxen",
    description = "Universal Software Packaging Platform — Build, dockerize, and ship any project",
    subcommands = {
        InitCommand.class,
        BuildCommand.class,
        PackageCommand.class,
        PublishCommand.class,
        DeployCommand.class,
        InfoCommand.class,
        DockerizeCommand.class,
        PublishDockerCommand.class
    },
    mixinStandardHelpOptions = true,
    version = "voxen 0.1.0-SNAPSHOT"
)
public class VoxenCli implements Runnable {

    @CommandLine.Option(
        names = {"-v", "--verbose"},
        description = "Verbose output"
    )
    public boolean verbose;

    @CommandLine.Option(
        names = {"--debug"},
        description = "Debug output"
    )
    public boolean debug;

    @CommandLine.Option(
        names = {"--json"},
        description = "Machine-readable JSON output"
    )
    public boolean json;

    @CommandLine.Option(
        names = {"-p", "--profile"},
        description = "Environment profile: dev, staging, prod, or custom profile name (default: dev)"
    )
    public String profile = "dev";

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new VoxenCli())
            .setExecutionExceptionHandler((ex, cmd, parseResult) -> {
                if (ex instanceof VoxenException ve) {
                    System.err.println("Error: " + ve.userMessage());
                    System.err.println("Hint:  " + ve.hint());
                } else if (ex instanceof CommandLine.ParameterException pe) {
                    System.err.println("Error: " + pe.getMessage());
                    System.err.println("Hint:  Run 'voxen " + cmd.getCommandName() + " --help' for usage.");
                } else {
                    System.err.println("Error: " + ex.getMessage());
                    System.err.println("Hint:  This is an unexpected error. Report it at https://github.com/lownoise/voxen/issues");
                }
                return 1;
            })
            .execute(args);
        System.exit(exitCode);
    }
}
