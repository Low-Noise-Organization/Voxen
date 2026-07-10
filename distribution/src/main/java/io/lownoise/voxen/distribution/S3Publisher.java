package io.lownoise.voxen.distribution;

import java.nio.file.Path;

public class S3Publisher implements Publisher {

    @Override
    public String name() {
        return "s3";
    }

    @Override
    public PublishResult publish(Path artifact, String repository, String groupId, String artifactId, String version) {
        CredentialManager.CredentialEntry creds = CredentialManager.get("s3");
        if (creds == null) {
            creds = CredentialManager.fromEnvironment("S3");
        }
        if (creds == null) {
            return PublishResult.failure(
                "No credentials found for S3.\n" +
                "Set S3_HOST/TOKEN environment variables or configure ~/.voxen/credentials.json");
        }

        String bucket = repository != null ? repository : "voxen-releases";
        String key = groupId.replace('.', '/') + "/" + artifactId + "/" + version + "/" + artifact.getFileName();

        try {
            var process = new ProcessBuilder(
                "aws", "s3", "cp",
                artifact.toAbsolutePath().toString(),
                "s3://" + bucket + "/" + key
            )
                .inheritIO()
                .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                .redirectError(ProcessBuilder.Redirect.DISCARD)
                .start();

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                ChecksumGenerator.writeChecksumFiles(artifact);
                return PublishResult.success(
                    "s3://" + bucket + "/" + key,
                    "https://" + bucket + ".s3.amazonaws.com/" + key,
                    "Published to S3: s3://" + bucket + "/" + key);
            } else {
                return PublishResult.failure("AWS CLI failed with exit code " + exitCode);
            }
        } catch (Exception e) {
            return PublishResult.failure("S3 publish failed: " + e.getMessage()
                + "\nEnsure the AWS CLI is installed and configured.");
        }
    }
}
