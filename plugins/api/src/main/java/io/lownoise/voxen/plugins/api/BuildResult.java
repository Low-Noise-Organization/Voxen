package io.lownoise.voxen.plugins.api;

import java.nio.file.Path;
import java.util.List;

public record BuildResult(
    boolean success,
    List<Path> artifacts,
    long durationMs,
    String output
) {

    public static BuildResult success(List<Path> artifacts, long durationMs, String output) {
        return new BuildResult(true, List.copyOf(artifacts), durationMs, output);
    }

    public static BuildResult failure(String errorMessage) {
        return new BuildResult(false, List.of(), 0, errorMessage);
    }
}
