package io.lownoise.voxen.distribution;

public record PublishResult(
    boolean success,
    String target,
    String url,
    String message
) {
    public static PublishResult success(String target, String url, String message) {
        return new PublishResult(true, target, url, message);
    }

    public static PublishResult failure(String message) {
        return new PublishResult(false, null, null, message);
    }
}
