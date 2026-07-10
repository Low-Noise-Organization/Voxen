package io.lownoise.voxen.core;

import io.lownoise.voxen.plugins.api.VoxenException;

import java.nio.file.Path;

public record ProjectConfig(
    String name,
    String runtime,
    String target,
    String output,
    Path projectDir
) {

    public ProjectConfig {
        if (name == null || name.isBlank()) {
            throw new VoxenException(
                "Project name must not be blank.",
                "Provide a name with --name or let voxen use the directory name."
            );
        }
        if (runtime == null || runtime.isBlank()) {
            throw new VoxenException(
                "Runtime must not be blank.",
                "Provide a runtime with --runtime (e.g. java, kotlin, rust, node, python, go, dotnet)."
            );
        }
        if (projectDir == null) {
            throw new VoxenException(
                "Project directory must not be null.",
                "This is an internal error. Report it at https://github.com/lownoise/voxen/issues"
            );
        }
    }

    public Path outputDirectory() {
        if (output != null && !output.isBlank()) {
            return projectDir.resolve(output);
        }
        return projectDir.resolve("dist");
    }

    public ProjectConfig withProjectDir(Path projectDir) {
        return new ProjectConfig(name, runtime, target, output, projectDir);
    }
}
