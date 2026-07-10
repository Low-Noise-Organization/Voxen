package io.lownoise.voxen.distribution;

public record DeployResult(
    boolean success,
    String target,
    String url,
    String message
) {
    public static DeployResult success(String target, String url, String message) {
        return new DeployResult(true, target, url, message);
    }

    public static DeployResult failure(String message) {
        return new DeployResult(false, null, null, message);
    }
}
