package io.lownoise.voxen.plugins.api;

public record DeployResult(
    boolean success,
    String targetEnvironment,
    String deploymentUrl,
    String output
) {

    public static DeployResult success(String targetEnvironment, String deploymentUrl, String output) {
        return new DeployResult(true, targetEnvironment, deploymentUrl, output);
    }

    public static DeployResult failure(String errorMessage) {
        return new DeployResult(false, null, null, errorMessage);
    }
}
