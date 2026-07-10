package io.lownoise.voxen.plugins.api;

public record PublishResult(
    boolean success,
    String targetRepository,
    String artifactUrl,
    String output
) {

    public static PublishResult success(String targetRepository, String artifactUrl, String output) {
        return new PublishResult(true, targetRepository, artifactUrl, output);
    }

    public static PublishResult failure(String errorMessage) {
        return new PublishResult(false, null, null, errorMessage);
    }
}
