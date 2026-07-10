package io.lownoise.voxen.cli.command;

import io.lownoise.voxen.cli.OutputFormatter;
import io.lownoise.voxen.core.AuditLogger;
import io.lownoise.voxen.core.ConsoleProgress;
import io.lownoise.voxen.distribution.PublishResult;
import io.lownoise.voxen.docker.DockerPublisher;
import picocli.CommandLine;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(
    name = "publish:docker",
    description = "Publish a Docker image to a registry",
    mixinStandardHelpOptions = true
)
public class PublishDockerCommand implements Callable<Integer> {

    @CommandLine.ParentCommand
    io.lownoise.voxen.cli.VoxenCli parent;

    @CommandLine.Option(
        names = {"-t", "--tag"},
        description = "Local image tag to publish",
        required = true
    )
    String localTag;

    @CommandLine.Option(
        names = {"-r", "--registry"},
        description = "Target registry (e.g. ghcr.io/user)",
        required = true
    )
    String registry;

    @CommandLine.Option(
        names = {"-n", "--name"},
        description = "Image name",
        required = true
    )
    String imageName;

    @CommandLine.Option(
        names = {"--version"},
        description = "Image version tag",
        defaultValue = "latest"
    )
    String version;

    @CommandLine.Option(
        names = {"--platform"},
        description = "Platforms for multi-arch (e.g. linux/amd64,linux/arm64)",
        split = ","
    )
    List<String> platforms;

    @CommandLine.Parameters(
        paramLabel = "directory",
        arity = "0..1"
    )
    Path directory;

    @Override
    public Integer call() {
        OutputFormatter fmt = new OutputFormatter(parent != null && parent.json);
        boolean json = parent != null && parent.json;
        Path dir = directory != null ? directory : Path.of(".");
        AuditLogger audit = new AuditLogger(dir);
        ConsoleProgress progress = new ConsoleProgress(!json);

        String remoteTag = registry + "/" + imageName + ":" + version;
        fmt.log("Publishing " + localTag + " -> " + remoteTag + "...");

        DockerPublisher publisher = new DockerPublisher();

        progress.showSpinner("Publishing...", json);

        PublishResult result;
        if (platforms != null && !platforms.isEmpty()) {
            result = publisher.publishMultiArch(
                Path.of(localTag), registry, imageName, version, platforms);
        } else {
            result = publisher.publish(
                Path.of(localTag), registry, imageName, imageName, version);
        }

        if (result.success()) {
            progress.done("Published: " + remoteTag, json);
            audit.record("publish:docker", imageName, "success",
                "tag=" + remoteTag + ", registry=" + registry);
            fmt.result(new OutputFormatter.PublishJsonResult(
                "success", imageName, registry, remoteTag));
            fmt.log("Published to: " + remoteTag);
            return 0;
        } else {
            progress.fail("Publishing failed", json);
            audit.record("publish:docker", imageName, "failure",
                "registry=" + registry + ", error=" + result.message());
            fmt.result(new OutputFormatter.PublishJsonResult(
                "failure", imageName, "", null));
            fmt.error("Failed: " + result.message());
            return 1;
        }
    }
}
