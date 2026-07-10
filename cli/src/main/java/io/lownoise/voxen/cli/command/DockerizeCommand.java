package io.lownoise.voxen.cli.command;

import io.lownoise.voxen.cli.OutputFormatter;
import io.lownoise.voxen.core.AuditLogger;
import io.lownoise.voxen.core.ConsoleProgress;
import io.lownoise.voxen.core.PluginLoader;
import io.lownoise.voxen.core.ProfileConfig;
import io.lownoise.voxen.core.Project;
import io.lownoise.voxen.docker.*;
import io.lownoise.voxen.plugins.api.PluginContext;
import picocli.CommandLine;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(
    name = "dockerize",
    description = "Generate an optimized Docker image from the built artifact",
    mixinStandardHelpOptions = true
)
public class DockerizeCommand implements Callable<Integer> {

    @CommandLine.ParentCommand
    io.lownoise.voxen.cli.VoxenCli parent;

    @CommandLine.Option(
        names = {"-t", "--tag"},
        description = "Image tag (e.g. ghcr.io/user/app:1.0.0)",
        required = true
    )
    String tag;

    @CommandLine.Option(
        names = {"--push"},
        description = "Push image to registry after build"
    )
    boolean push;

    @CommandLine.Option(
        names = {"--platform"},
        description = "Target platforms (e.g. linux/amd64,linux/arm64)",
        split = ","
    )
    List<String> platforms;

    @CommandLine.Option(
        names = {"--native"},
        description = "Use native-image entrypoint"
    )
    boolean nativeImage;

    @CommandLine.Option(
        names = {"--lint"},
        description = "Run image linter after build"
    )
    boolean lint;

    @CommandLine.Option(
        names = {"--sign"},
        description = "Sign image with cosign (keyless)"
    )
    boolean sign;

    @CommandLine.Parameters(
        paramLabel = "directory",
        description = "Project directory (default: current directory)",
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

        var plugins = PluginLoader.loadAll();
        var context = createContext(dir);
        Project project = Project.detect(dir, plugins, context);

        String profileName = parent != null ? parent.profile : "dev";
        ProfileConfig profile;
        try {
            profile = ProfileConfig.load(dir, profileName);
        } catch (Exception e) {
            profile = null;
        }

        fmt.log("Dockerizing " + project.name() + " (" + tag + ")...");

        progress.showSpinner("Resolving base image...", json);
        BaseImageResolver resolver = new BaseImageResolver();
        BaseImageResolver.BaseImage base = resolver.resolve(project.config().runtime(), nativeImage);
        fmt.verbose(parent != null && parent.verbose, "Base image: " + base.fullName());

        progress.showSpinner("Loading build metadata...", json);
        BuildMetadata metadata = buildMetadata(project, profile, dir);

        progress.showSpinner("Generating Dockerfile...", json);
        DockerfileGenerator dfGen = new DockerfileGenerator(resolver);
        String dockerfile = dfGen.generate(project.config(), metadata);
        Path dfPath = dir.resolve("Dockerfile");
        try {
            Files.writeString(dfPath, dockerfile);
        } catch (Exception e) {
            fmt.error("Failed to write Dockerfile: " + e.getMessage());
            return 1;
        }

        progress.showSpinner("Generating .dockerignore...", json);
        generateDockerignore(dir);

        progress.showSpinner("Building Docker image...", json);
        int buildExit;
        if (platforms != null && !platforms.isEmpty()) {
            MultiArchBuilder multiArch = new MultiArchBuilder();
            buildExit = multiArch.build(dir, tag, platforms, push, dfPath.toString());
        } else {
            buildExit = runDockerBuild(dir, tag, dfPath);
        }

        if (buildExit != 0) {
            progress.fail("Docker build failed (exit=" + buildExit + ")", json);
            audit.record("dockerize", project.name(), "failure",
                "tag=" + tag + ", exit=" + buildExit);
            return 1;
        }

        if (push && (platforms == null || platforms.isEmpty())) {
            progress.showSpinner("Pushing image to registry...", json);
            DockerPublisher publisher = new DockerPublisher();
            var result = publisher.publish(Path.of(tag), registryFromTag(tag),
                project.config().name(), project.config().name(), versionFromTag(tag));
            if (!result.success()) {
                progress.fail("Push failed", json);
                audit.record("dockerize:push", project.name(), "failure",
                    "tag=" + tag + ", " + result.message());
                return 1;
            }
            progress.done("Image pushed: " + tag, json);
        }

        if (sign) {
            progress.showSpinner("Signing image with Cosign...", json);
            ManifestSigner signer = new ManifestSigner();
            if (signer.signWithCosignKeyless(tag)) {
                fmt.log("Image signed successfully");
            } else {
                fmt.verbose(parent != null && parent.verbose, "Cosign signing skipped or failed");
            }
        }

        if (lint) {
            progress.showSpinner("Linting image...", json);
            ImageLinter linter = new ImageLinter();
            ImageLinter.LintResult lintResult = linter.lint(tag);
            if (!lintResult.errors().isEmpty()) {
                for (String err : lintResult.errors()) {
                    fmt.error("  LINT ERROR: " + err);
                }
            }
            if (!lintResult.warnings().isEmpty()) {
                for (String warn : lintResult.warnings()) {
                    fmt.verbose(parent != null && parent.verbose, "  LINT: " + warn);
                }
            }
        }

        progress.done("Docker image ready: " + tag, json);
        audit.record("dockerize", project.name(), "success",
            "tag=" + tag + ", push=" + push + ", native=" + nativeImage);

        fmt.result(new OutputFormatter.DockerizeJsonResult(
            "success", project.name(), tag, dfPath.toString(), push));
        return 0;
    }

    private BuildMetadata buildMetadata(Project project, ProfileConfig profile, Path dir) {
        BuildMetadata.Builder builder = BuildMetadata.builder()
            .projectName(project.name())
            .runtime(project.config().runtime())
            .pluginName("java")
            .profile(parent != null ? parent.profile : "dev")
            .nativeImage(nativeImage)
            .artifactName(project.config().target() != null
                ? project.name() + "." + project.config().target()
                : project.name() + ".jar");

        if (profile != null) {
            builder.description("Profile: " + profile.name());
        }

        try {
            String gitCommit = exec("git", "rev-parse", "--short", "HEAD");
            builder.commit(gitCommit != null ? gitCommit.trim() : "");
        } catch (Exception e) {
            builder.commit("");
        }

        try {
            String gitRemote = exec("git", "config", "--get", "remote.origin.url");
            builder.repository(gitRemote != null ? gitRemote.trim() : "");
        } catch (Exception e) {
            builder.repository("");
        }

        Path checksumPath = dir.resolve("dist").resolve(builder.build().artifactName() + ".sha256");
        if (Files.exists(checksumPath)) {
            try {
                String sha = Files.readString(checksumPath).trim();
                builder.checksumSha256(sha);
                builder.signed(true);
            } catch (Exception ignored) {}
        }

        return builder.build();
    }

    private void generateDockerignore(Path dir) {
        try {
            Path df = dir.resolve(".dockerignore");
            if (!Files.exists(df)) {
                String content = """
                    .git/
                    .gradle/
                    build/
                    .voxen/
                    *.md
                    .gitignore
                    .dockerignore
                    voxen.json
                    """.trim();
                Files.writeString(df, content);
            }
        } catch (Exception ignored) {}
    }

    private int runDockerBuild(Path dir, String tag, Path dockerfile) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                "docker", "buildx", "build",
                "-t", tag,
                "-f", dockerfile.toString(),
                "--label", "voxen.generated=true",
                dir.toString()
            ).inheritIO();
            Process p = pb.start();
            return p.waitFor();
        } catch (Exception e) {
            System.err.println("Docker build failed: " + e.getMessage());
            return 1;
        }
    }

    private String registryFromTag(String tag) {
        int slash = tag.indexOf('/');
        return slash > 0 ? tag.substring(0, slash) : "docker.io";
    }

    private String versionFromTag(String tag) {
        int colon = tag.lastIndexOf(':');
        return colon > 0 ? tag.substring(colon + 1) : "latest";
    }

    private String exec(String... cmd) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(cmd)
            .redirectErrorStream(true);
        Process p = pb.start();
        String out = new String(p.getInputStream().readAllBytes()).trim();
        p.waitFor();
        return out;
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
