package io.lownoise.voxen.distribution;

import java.nio.file.Path;

public class NexusPublisher extends HttpPublisher implements Publisher {

    @Override
    public String name() {
        return "nexus";
    }

    @Override
    public PublishResult publish(Path artifact, String repository, String groupId, String artifactId, String version) {
        CredentialManager.CredentialEntry creds = CredentialManager.get("nexus");
        if (creds == null) {
            creds = CredentialManager.fromEnvironment("NEXUS");
        }
        if (creds == null) {
            return PublishResult.failure(
                "No credentials found for Nexus.\n" +
                "Set NEXUS_HOST/TOKEN environment variables or configure ~/.voxen/credentials.json");
        }

        String repo = repository != null ? repository : "releases";
        String baseUrl = creds.host();
        String url = baseUrl + "/repository/" + repo + "/"
            + groupId.replace('.', '/') + "/"
            + artifactId + "/" + version + "/" + artifact.getFileName();

        PublishResult result = upload(url, artifact, creds.token());
        if (result.success()) {
            ChecksumGenerator.writeChecksumFiles(artifact);
        }
        return result;
    }
}
